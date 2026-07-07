package com.example.educationapp.presentation.screenmodel.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.util.asUiText
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetAssignmentRemindersUseCase
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.domain.usecase.GetMyCoursesUseCase
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesInfoUseCase
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.error_unknown
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow<StudentDashboardState>(StudentDashboardState.Loading)
    val state: StateFlow<StudentDashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        screenModelScope.launch {
            _state.value = StudentDashboardState.Loading

            // 1. Get profile to find studentId
            when (val profileResult = getMyProfileUseCase(AppRole.STUDENT)) {
                is ApiResult.Error -> {
                    _state.value = StudentDashboardState.Error(profileResult.asUiText())
                }

                is ApiResult.Success -> {
                    val profile = profileResult.data
                    if (profile is UserProfile.Student) {
                        val studentId = profile.studentId.toLong()

                        // Calculate dates (From now to 2 days later 23:59:59)
                        val now =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val today = now.date
                        val dayPlus2 = LocalDate.fromEpochDays(today.toEpochDays() + 2)

                        val fromTime = "${now.date}T" +
                                "${now.hour.toString().padStart(2, '0')}:" +
                                "${now.minute.toString().padStart(2, '0')}:" +
                                "${now.second.toString().padStart(2, '0')}"
                        val toTime = "${dayPlus2}T23:59:59"

                        // Fetch concurrent dashboard components
                        val schedulesDeferred = async { getMySchedulesUseCase(fromTime, toTime) }
                        val coursesDeferred =
                            async { getMyCoursesUseCase(isActive = true, page = 0, size = 100) }
                        val classesDeferred = async {
                            getStudentClassesInfoUseCase(
                                studentId = studentId,
                                page = 0,
                                size = 100
                            )
                        }
                        val remindersDeferred = async {
                            getAssignmentRemindersUseCase(
                                studentId = studentId.toInt(),
                                classId = null,
                                dueInHours = 48,
                                page = 0,
                                size = 50
                            )
                        }

                        val schedulesResult = schedulesDeferred.await()
                        val coursesResult = coursesDeferred.await()
                        val classesResult = classesDeferred.await()
                        val remindersResult = remindersDeferred.await()

                        if (schedulesResult is ApiResult.Success &&
                            coursesResult is ApiResult.Success &&
                            classesResult is ApiResult.Success &&
                            remindersResult is ApiResult.Success
                        ) {
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

                            // Map attendance info
                            val attendanceByClass =
                                attendanceResults.mapNotNull { (studentClass, rateResult) ->
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

                            // Extract teacher contacts
                            val teacherContacts = classesList.map { studentClass ->
                                TeacherContactUiModel(
                                    className = studentClass.className,
                                    courseName = studentClass.courseName,
                                    teacherEmail = studentClass.teacherEmail,
                                    teacherPhone = studentClass.teacherPhone
                                )
                            }.filter { it.teacherEmail != null || it.teacherPhone != null }

                            // Map schedules to ScheduleSessionUiModel
                            val uiSchedules = schedulesResult.data.map { item ->
                                val date = try {
                                    LocalDateTime.parse(item.startTime).date
                                } catch (e: Exception) {
                                    LocalDate(2026, 6, 1)
                                }

                                val roomText =
                                    if (item.roomName.isNotBlank() && item.roomName != "string") {
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

                            _state.value = StudentDashboardState.Success(
                                upcomingSchedules = uiSchedules,
                                assignmentReminders = remindersResult.data.content,
                                attendanceByClass = attendanceByClass,
                                teacherContacts = teacherContacts,
                                currentCourses = coursesResult.data.content
                            )
                        } else {
                            val firstError = listOf(
                                schedulesResult,
                                coursesResult,
                                classesResult,
                                remindersResult
                            )
                                .filterIsInstance<ApiResult.Error>()
                                .firstOrNull()

                            _state.value = StudentDashboardState.Error(
                                firstError?.asUiText()
                                    ?: UiText.ResourceString(Res.string.error_unknown)
                            )
                        }
                    } else {
                        _state.value = StudentDashboardState.Error(
                            UiText.DynamicString("Tài khoản không phải học sinh.")
                        )
                    }
                }
            }
        }
    }
}
