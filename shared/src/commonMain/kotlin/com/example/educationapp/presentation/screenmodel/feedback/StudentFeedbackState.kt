package com.example.educationapp.presentation.screenmodel.feedback

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.StudentClassFeedback

sealed interface StudentFeedbackState {
    object Loading : StudentFeedbackState
    data class Success(
        val feedback: StudentClassFeedback?,
        val isSubmitting: Boolean = false,
        val submitError: UiText? = null
    ) : StudentFeedbackState
    data class Error(val message: UiText) : StudentFeedbackState
}
