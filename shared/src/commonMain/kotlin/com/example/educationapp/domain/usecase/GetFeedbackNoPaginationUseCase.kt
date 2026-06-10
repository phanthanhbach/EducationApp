package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository

class GetFeedbackNoPaginationUseCase(
    private val repository: ClassFeedbackRepository
) {
    suspend operator fun invoke(
        classId: Long,
        studentId: Long,
        feedbackType: String
    ): ApiResult<Feedback?> {
        return repository.getFeedbackNoPagination(classId, studentId, feedbackType)
    }
}
