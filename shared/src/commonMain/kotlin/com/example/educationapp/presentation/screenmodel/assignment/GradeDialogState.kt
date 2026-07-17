package com.example.educationapp.presentation.screenmodel.assignment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SubmissionDetail

sealed interface GradeDialogState {
    data object Idle : GradeDialogState
    data class Visible(
        val submission: SubmissionDetail,
        val isLoading: Boolean = false,
        val errorMessage: UiText? = null
    ) : GradeDialogState
}
