package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.SubmissionDetail
import com.example.educationapp.domain.repository.AssignmentRepository

class FilterAssignmentSubmissionsUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(
        assignmentId: Int,
        classId: Int,
        submitted: Boolean? = null,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SubmissionDetail>> {
        return repository.filterSubmissions(assignmentId, classId, submitted, page, size)
    }
}
