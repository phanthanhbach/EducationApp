package com.example.educationapp.presentation.screenmodel.session_detail

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.presentation.model.ScheduleSessionUiModel

sealed interface SessionDetailState {
    object Loading : SessionDetailState
    data class NotCheckedIn(val session: ScheduleSessionUiModel) : SessionDetailState
    data class CheckedIn(
        val session: ScheduleSessionUiModel,
        val checkInInfo: TeacherCheckInResult
    ) : SessionDetailState

    data class Error(val message: UiText) : SessionDetailState
}