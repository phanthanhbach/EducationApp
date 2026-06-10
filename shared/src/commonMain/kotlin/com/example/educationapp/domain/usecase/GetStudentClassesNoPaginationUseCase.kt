package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.repository.ScheduleRepository

class GetStudentClassesNoPaginationUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        studentId: Long,
        status: String? = null
    ): ApiResult<List<SchoolClass>> {
        return repository.getStudentClassesNoPagination(studentId, status)
    }
}
