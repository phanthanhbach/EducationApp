package com.example.educationapp.presentation.screenmodel.schedule

import com.example.educationapp.core.util.UiText
import com.example.educationapp.presentation.model.ScheduleSessionUiModel

sealed interface ScheduleState {
    object Idle : ScheduleState
    object Loading : ScheduleState
    data class Success(val schedules: List<ScheduleSessionUiModel>) : ScheduleState
    data class Error(val error: UiText) : ScheduleState
}