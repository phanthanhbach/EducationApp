package com.example.educationapp.presentation.screenmodel.assignment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.StudentAssignment

sealed interface StudentClassAssignmentsState {
    object Loading : StudentClassAssignmentsState
    data class Success(
        val assignments: List<StudentAssignment>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : StudentClassAssignmentsState
    data class Error(val message: UiText) : StudentClassAssignmentsState
}
