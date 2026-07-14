package com.example.educationapp.presentation.screenmodel.parent

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.SchoolClass

sealed interface ChildAttendanceRateState {
    object Loading : ChildAttendanceRateState
    data class Success(
        val classes: List<SchoolClass>,
        val rates: Map<Long, AttendanceRate>,
        val summaryTotal: Int,
        val summaryAttended: Int,
        val summaryAbsent: Int,
        val summaryRate: Double,
        val currentPage: Int = 0,
        val totalPages: Int = 1,
        val hasNextPage: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : ChildAttendanceRateState

    data class Error(val message: UiText) : ChildAttendanceRateState
}
