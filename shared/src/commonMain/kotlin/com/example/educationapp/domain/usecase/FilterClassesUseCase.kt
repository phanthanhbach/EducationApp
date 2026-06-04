package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.repository.ScheduleRepository

class FilterClassesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        search: String? = null,
        courseId: Long? = null,
        teacherId: Long? = null,
        branchId: Long? = null,
        status: String? = null,
        page: Int = 0,
        size: Int = 20
    ): ApiResult<PaginationResponse<SchoolClass>> {
        return repository.filterClasses(search, courseId, teacherId, branchId, status, page, size)
    }
}
