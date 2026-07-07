package com.example.educationapp.presentation.screenmodel.dashboard

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.presentation.model.ScheduleSessionUiModel

sealed interface TeacherDashboardState {
    object Loading : TeacherDashboardState
    data class Success(
        val ratingSummary: TeacherRatingSummary,
        val upcomingSchedules: List<ScheduleSessionUiModel>,
        val totalCheckIns: Int,
        val recentCheckIns: List<TeacherCheckInResult>
    ) : TeacherDashboardState

    data class Error(val error: UiText) : TeacherDashboardState
}
