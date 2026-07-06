package com.example.educationapp.presentation.screenmodel.feedback

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.StudentClassFeedback

sealed interface ClassFeedbackState {
    object Loading : ClassFeedbackState
    data class Success(
        val feedbacks: List<StudentClassFeedback>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val submittingFeedbackKeys: Set<String> = emptySet(),
        val submitErrorMessage: UiText? = null
    ) : ClassFeedbackState
    data class Error(val message: UiText) : ClassFeedbackState
}
