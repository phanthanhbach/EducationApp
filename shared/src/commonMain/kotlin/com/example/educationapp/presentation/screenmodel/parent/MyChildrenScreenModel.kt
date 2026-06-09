package com.example.educationapp.presentation.screenmodel.parent

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.usecase.GetMySchedulesUseCase
import com.example.educationapp.domain.usecase.GetMyCoursesUseCase
import com.example.educationapp.domain.usecase.GetAssignmentRemindersUseCase
import com.example.educationapp.domain.usecase.GetStudentClassesInfoUseCase
import com.example.educationapp.domain.usecase.GetAttendanceRateUseCase
import com.example.educationapp.presentation.screenmodel.dashboard.AttendanceByClassUiModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherContactUiModel
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
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

sealed interface MyChildrenState {
    object Idle : MyChildrenState
    object Loading : MyChildrenState
    data class Success(
        val upcomingSchedules: List<ScheduleSessionUiModel>,
        val assignmentReminders: List<AssignmentReminder>,
        val attendanceByClass: List<AttendanceByClassUiModel>,
        val teacherContacts: List<TeacherContactUiModel>,
        val currentCourses: List<Course>
    ) : MyChildrenState
    data class Error(val message: String) : MyChildrenState
}

class MyChildrenScreenModel(
    private val getMySchedulesUseCase: GetMySchedulesUseCase,
    private val getMyCoursesUseCase: GetMyCoursesUseCase,
    private val getAssignmentRemindersUseCase: GetAssignmentRemindersUseCase,
    private val getStudentClassesInfoUseCase: GetStudentClassesInfoUseCase,
    private val getAttendanceRateUseCase: GetAttendanceRateUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<MyChildrenState>(MyChildrenState.Idle)
    val state: StateFlow<MyChildrenState> = _state.asStateFlow()

    fun loadDashboardData(studentId: Long) {
        screenModelScope.launch {
            _state.value = MyChildrenState.Loading

            // Calculate dates (From now to 2 days later 23:59:59)
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val today = now.date
            val dayPlus2 = LocalDate.fromEpochDays(today.toEpochDays() + 2)

            val fromTime = "${now.date}T" +
                    "${now.hour.toString().padStart(2, '0')}:" +
                    "${now.minute.toString().padStart(2, '0')}:" +
                    "${now.second.toString().padStart(2, '0')}"
            val toTime = "${dayPlus2}T23:59:59"

            // Fetch concurrent dashboard components using studentId
            val schedulesDeferred = async { getMySchedulesUseCase(fromTime, toTime) }
            val coursesDeferred = async { getMyCoursesUseCase(isActive = true, page = 0, size = 100) } // courses does not take studentId, is global
            val classesDeferred = async { getStudentClassesInfoUseCase(studentId = studentId, page = 0, size = 100) }
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
                val attendanceByClass = attendanceResults.mapNotNull { (studentClass, rateResult) ->
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

                _state.value = MyChildrenState.Success(
                    upcomingSchedules = uiSchedules,
                    assignmentReminders = remindersResult.data.content,
                    attendanceByClass = attendanceByClass,
                    teacherContacts = teacherContacts,
                    currentCourses = coursesResult.data.content
                )
            } else {
                val errorMsg = listOf(
                    (schedulesResult as? ApiResult.Error)?.message,
                    (coursesResult as? ApiResult.Error)?.message,
                    (classesResult as? ApiResult.Error)?.message,
                    (remindersResult as? ApiResult.Error)?.message
                ).firstOrNull { it != null } ?: "Lỗi tải dữ liệu học sinh."

                _state.value = MyChildrenState.Error(errorMsg)
            }
        }
    }
}
