package com.example.educationapp.presentation.screenmodel.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetAssignmentRemindersUseCase
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.GetMyCoursesUseCase
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesInfoUseCase
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class StudentDashboardScreenModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getMySchedulesUseCase: GetMySchedulesUseCase,
    private val getAttendanceRateUseCase: GetAttendanceRateUseCase,
    private val getMyCoursesUseCase: GetMyCoursesUseCase,
    private val getAssignmentRemindersUseCase: GetAssignmentRemindersUseCase,
    private val getStudentClassesInfoUseCase: GetStudentClassesInfoUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(StudentDashboardState())
    val state: StateFlow<StudentDashboardState> = _state.asStateFlow()

    // Cached variables to allow retrying without re-fetching profile
    private var cachedStudentId: Long? = null
    private var cachedFromTime: String? = null
    private var cachedToTime: String? = null

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        screenModelScope.launch {
            _state.update {
                StudentDashboardState(
                    isProfileLoading = true,
                    profileError = null,
                    studentProfile = null
                )
            }

            // 1. Get profile to find studentId
            when (val profileResult = getMyProfileUseCase(AppRole.STUDENT)) {
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            isProfileLoading = false,
                            profileError = profileResult.asUiText()
                        )
                    }
                }

                is ApiResult.Success -> {
                    val profile = profileResult.data
                    if (profile is UserProfile.Student) {
                        val studentId = profile.studentId.toLong()
                        cachedStudentId = studentId

                        // Calculate dates (From now to 2 days later 23:59:59)
                        val now =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val today = now.date
                        val dayPlus2 = LocalDate.fromEpochDays(today.toEpochDays() + 2)

                        val fromTime = "${now.date}T" +
                                "${now.hour.toString().padStart(2, '0')}:" +
                                "${now.minute.toString().padStart(2, '0')}:" +
                                now.second.toString().padStart(2, '0')
                        val toTime = "${dayPlus2}T23:59:59"

                        cachedFromTime = fromTime
                        cachedToTime = toTime

                        _state.update {
                            it.copy(
                                isProfileLoading = false,
                                studentProfile = profile
                            )
                        }

                        // Load sections in parallel
                        loadSchedules(fromTime, toTime)
                        loadAssignments(studentId)
                        loadAttendanceAndTeacherContacts(studentId)
                        loadCourses()
                    } else {
                        _state.update {
                            it.copy(
                                isProfileLoading = false,
                                profileError = UiText.DynamicString("Tài khoản không phải học sinh.")
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadSchedules(fromTime: String, toTime: String) {
        screenModelScope.launch {
            _state.update { it.copy(schedulesState = DashboardSectionState.Loading) }
            val result = getMySchedulesUseCase(fromTime, toTime)
            if (result is ApiResult.Success) {
                val uiSchedules = result.data.map { item ->
                    val date = try {
                        LocalDateTime.parse(item.startTime).date
                    } catch (_: Exception) {
                        LocalDate(2026, 6, 1)
                    }

                    val roomText = if (item.roomName.isNotBlank() && item.roomName != "string") {
                        item.roomName
                    } else {
                        "Phòng ${item.roomId}"
                    }

                    ScheduleSessionUiModel(
                        id = "${item.classId}_${item.sessionNumber}_${item.startTime}",
                        classId = item.classId,
                        sessionNumber = item.sessionNumber,
                        subjectName = if (!item.notes.isNullOrBlank() && item.notes != "string") item.notes else item.className,
                        className = item.className,
                        room = roomText,
                        startTimeRaw = item.startTime,
                        endTimeRaw = item.endTime,
                        attendanceText = "",
                        date = date
                    )
                }.sortedBy { it.startTimeRaw }
                _state.update { it.copy(schedulesState = DashboardSectionState.Success(uiSchedules)) }
            } else if (result is ApiResult.Error) {
                _state.update { it.copy(schedulesState = DashboardSectionState.Error(result.asUiText())) }
            }
        }
    }

    private fun loadAssignments(studentId: Long) {
        screenModelScope.launch {
            _state.update { it.copy(assignmentsState = DashboardSectionState.Loading) }
            val result = getAssignmentRemindersUseCase(
                studentId = studentId.toInt(),
                classId = null,
                dueInHours = 48,
                page = 0,
                size = 50
            )
            if (result is ApiResult.Success) {
                _state.update { it.copy(assignmentsState = DashboardSectionState.Success(result.data.content)) }
            } else if (result is ApiResult.Error) {
                _state.update { it.copy(assignmentsState = DashboardSectionState.Error(result.asUiText())) }
            }
        }
    }

    private fun loadAttendanceAndTeacherContacts(studentId: Long) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    attendanceState = DashboardSectionState.Loading,
                    teacherContactsState = DashboardSectionState.Loading
                )
            }
            val classesResult = getStudentClassesInfoUseCase(
                studentId = studentId,
                page = 0,
                size = 100
            )
            if (classesResult is ApiResult.Success) {
                val classesList = classesResult.data

                // Fetch attendance rates in parallel for each class
                val attendanceDeferredList = classesList.map { studentClass ->
                    async {
                        val rateResult = getAttendanceRateUseCase(
                            studentId = studentId.toInt(),
                            courseClassId = studentClass.classId.toInt()
                        )
                        studentClass to rateResult
                    }
                }
                val attendanceResults = attendanceDeferredList.awaitAll()

                val attendanceByClass = attendanceResults.map { (studentClass, rateResult) ->
                    if (rateResult is ApiResult.Success) {
                        val data = rateResult.data
                        AttendanceByClassUiModel(
                            classId = studentClass.classId,
                            className = studentClass.className,
                            courseName = studentClass.courseName,
                            attendedSessions = data.attendedSessions,
                            totalSessions = data.totalSessions,
                            attendanceRate = data.attendanceRate
                        )
                    } else {
                        AttendanceByClassUiModel(
                            classId = studentClass.classId,
                            className = studentClass.className,
                            courseName = studentClass.courseName,
                            attendedSessions = 0,
                            totalSessions = 0,
                            attendanceRate = 0.0
                        )
                    }
                }

                val teacherContacts = classesList.map { studentClass ->
                    TeacherContactUiModel(
                        className = studentClass.className,
                        courseName = studentClass.courseName,
                        teacherEmail = studentClass.teacherEmail,
                        teacherPhone = studentClass.teacherPhone
                    )
                }.filter { it.teacherEmail != null || it.teacherPhone != null }

                _state.update {
                    it.copy(
                        attendanceState = DashboardSectionState.Success(attendanceByClass),
                        teacherContactsState = DashboardSectionState.Success(teacherContacts)
                    )
                }
            } else if (classesResult is ApiResult.Error) {
                val error = classesResult.asUiText()
                _state.update {
                    it.copy(
                        attendanceState = DashboardSectionState.Error(error),
                        teacherContactsState = DashboardSectionState.Error(error)
                    )
                }
            }
        }
    }

    private fun loadCourses() {
        screenModelScope.launch {
            _state.update { it.copy(coursesState = DashboardSectionState.Loading) }
            val result = getMyCoursesUseCase(isActive = true, page = 0, size = 100)
            if (result is ApiResult.Success) {
                _state.update { it.copy(coursesState = DashboardSectionState.Success(result.data.content)) }
            } else if (result is ApiResult.Error) {
                _state.update { it.copy(coursesState = DashboardSectionState.Error(result.asUiText())) }
            }
        }
    }

    fun retrySchedules() {
        val fromTime = cachedFromTime
        val toTime = cachedToTime
        if (fromTime != null && toTime != null) {
            loadSchedules(fromTime, toTime)
        } else {
            loadDashboardData()
        }
    }

    fun retryAssignments() {
        val studentId = cachedStudentId
        if (studentId != null) {
            loadAssignments(studentId)
        } else {
            loadDashboardData()
        }
    }

    fun retryAttendance() {
        val studentId = cachedStudentId
        if (studentId != null) {
            loadAttendanceAndTeacherContacts(studentId)
        } else {
            loadDashboardData()
        }
    }

    fun retryTeacherContacts() {
        val studentId = cachedStudentId
        if (studentId != null) {
            loadAttendanceAndTeacherContacts(studentId)
        } else {
            loadDashboardData()
        }
    }

    fun retryCourses() {
        loadCourses()
    }
}
