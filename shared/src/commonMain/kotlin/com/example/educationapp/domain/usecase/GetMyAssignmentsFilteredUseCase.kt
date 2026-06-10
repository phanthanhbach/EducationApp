package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.domain.repository.AssignmentRepository

class GetMyAssignmentsFilteredUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(
        classId: Int,
        submitted: Boolean,
        page: Int,
        size: Int = 20
    ): ApiResult<PaginationResponse<StudentAssignment>> {
        return repository.getMyAssignmentsFiltered(classId, submitted, page, size)
    }
}
