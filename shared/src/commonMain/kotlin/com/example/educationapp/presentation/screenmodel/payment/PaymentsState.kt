package com.example.educationapp.presentation.screenmodel.payment

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SchoolClass

sealed interface PaymentsTabState {
    object Loading : PaymentsTabState
    data class Success(
        val classes: List<SchoolClass>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean,
        val studentId: Long,
        val isSearchingOrFiltering: Boolean = false
    ) : PaymentsTabState
    data class Error(val message: UiText) : PaymentsTabState
}
