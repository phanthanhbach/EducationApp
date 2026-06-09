package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.repository.AssignmentRepository

class FilterAssignmentsUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(
        classId: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Assignment>> {
        return repository.filterAssignments(classId, page, size)
    }
}
