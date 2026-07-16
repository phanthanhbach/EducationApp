package com.example.educationapp.presentation.screenmodel.assignment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SubmissionDetail

sealed interface AssignmentSubmissionsState {
    object Loading : AssignmentSubmissionsState
    data class Success(
        val submissions: List<SubmissionDetail>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : AssignmentSubmissionsState
    data class Error(val message: UiText) : AssignmentSubmissionsState
}
