package com.example.educationapp.presentation.screenmodel.attendance

import com.example.educationapp.core.util.UiText
import com.example.educationapp.presentation.model.AttendanceUiModel

sealed interface AttendanceState {
    object Loading : AttendanceState
    data class Loaded(
        val students: List<AttendanceUiModel>,
        val isSaving: Boolean = false,
        val hasChanges: Boolean = false
    ) : AttendanceState

    data class Error(val message: UiText) : AttendanceState
    object Saved : AttendanceState
}