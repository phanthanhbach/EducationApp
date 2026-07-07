package com.example.educationapp.presentation.screenmodel.assignment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SchoolClass

sealed interface AssignmentTabState {
    object Loading : AssignmentTabState
    data class Success(
        val classes: List<SchoolClass>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val isSearchingOrFiltering: Boolean = false
    ) : AssignmentTabState
    data class Error(val error: UiText) : AssignmentTabState
}
