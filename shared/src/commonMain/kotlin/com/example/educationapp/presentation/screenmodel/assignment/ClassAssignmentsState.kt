package com.example.educationapp.presentation.screenmodel.assignment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.Assignment

sealed interface ClassAssignmentsState {
    object Loading : ClassAssignmentsState
    data class Success(
        val assignments: List<Assignment>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : ClassAssignmentsState
    data class Error(val message: UiText) : ClassAssignmentsState
}
