package com.example.educationapp.presentation.screenmodel.course

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.Course

sealed interface MyCoursesState {
    object Loading : MyCoursesState
    data class Success(
        val courses: List<Course>,
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Int,
        val hasNextPage: Boolean
    ) : MyCoursesState
    data class Error(val message: UiText) : MyCoursesState
}
