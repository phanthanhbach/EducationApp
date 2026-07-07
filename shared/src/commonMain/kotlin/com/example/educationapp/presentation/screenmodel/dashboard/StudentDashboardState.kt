package com.example.educationapp.presentation.screenmodel.dashboard

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.presentation.model.ScheduleSessionUiModel

sealed interface StudentDashboardState {
    object Loading : StudentDashboardState
    data class Success(
        val upcomingSchedules: List<ScheduleSessionUiModel>,
        val assignmentReminders: List<AssignmentReminder>,
        val attendanceByClass: List<AttendanceByClassUiModel>,
        val teacherContacts: List<TeacherContactUiModel>,
        val currentCourses: List<Course>
    ) : StudentDashboardState

    data class Error(val error: UiText) : StudentDashboardState
}

data class AttendanceByClassUiModel(
    val classId: Long,
    val className: String,
    val courseName: String,
    val attendedSessions: Int,
    val totalSessions: Int,
    val attendanceRate: Double
)

data class TeacherContactUiModel(
    val className: String,
    val courseName: String,
    val teacherEmail: String?,
    val teacherPhone: String?
)
