package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository

class GetClassFeedbacksUseCase(
    private val repository: ClassFeedbackRepository
) {
    suspend operator fun invoke(
        classId: Long,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentClassFeedback>> {
        return repository.getClassFeedbacks(classId, page, size)
    }
}
