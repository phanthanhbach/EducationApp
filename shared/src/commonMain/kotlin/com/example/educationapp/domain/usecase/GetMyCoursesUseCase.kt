package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.repository.StudentDashboardRepository

class GetMyCoursesUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(
        search: String? = null,
        isActive: Boolean? = null,
        page: Int = 0,
        size: Int = 20
    ): ApiResult<PaginationResponse<Course>> {
        return repository.getMyCourses(search, isActive, page, size)
    }
}
