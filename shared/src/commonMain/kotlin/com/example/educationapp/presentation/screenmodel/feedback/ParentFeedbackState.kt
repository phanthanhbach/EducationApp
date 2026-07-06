package com.example.educationapp.presentation.screenmodel.feedback

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SchoolClass

sealed interface FeedbackClassesState {
    object Loading : FeedbackClassesState
    data class Success(
        val classes: List<SchoolClass>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val isSearchingOrFiltering: Boolean = false
    ) : FeedbackClassesState
    data class Error(val error: UiText) : FeedbackClassesState
}
