package com.example.educationapp.presentation.screenmodel.dashboard

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.presentation.model.ScheduleSessionUiModel

sealed interface DashboardSectionState<out T> {
    object Loading : DashboardSectionState<Nothing>
    data class Success<out T>(val data: T) : DashboardSectionState<T>
    data class Error(val error: UiText) : DashboardSectionState<Nothing>
}

data class StudentDashboardState(
    val isProfileLoading: Boolean = true,
    val profileError: UiText? = null,
    val studentProfile: UserProfile.Student? = null,
    val schedulesState: DashboardSectionState<List<ScheduleSessionUiModel>> = DashboardSectionState.Loading,
    val assignmentsState: DashboardSectionState<List<AssignmentReminder>> = DashboardSectionState.Loading,
    val attendanceState: DashboardSectionState<List<AttendanceByClassUiModel>> = DashboardSectionState.Loading,
    val teacherContactsState: DashboardSectionState<List<TeacherContactUiModel>> = DashboardSectionState.Loading,
    val coursesState: DashboardSectionState<List<Course>> = DashboardSectionState.Loading
)

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
