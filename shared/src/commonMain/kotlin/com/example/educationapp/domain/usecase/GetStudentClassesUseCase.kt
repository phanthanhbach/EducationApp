package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.repository.ScheduleRepository

class GetStudentClassesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        studentId: Long,
        status: String? = null,
        page: Int = 0,
        size: Int = 20
    ): ApiResult<PaginationResponse<SchoolClass>> {
        return repository.getStudentClasses(studentId, status, page, size)
    }
}
