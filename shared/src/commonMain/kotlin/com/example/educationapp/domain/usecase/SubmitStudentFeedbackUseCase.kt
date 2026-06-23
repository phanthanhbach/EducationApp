package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository

class SubmitStudentFeedbackUseCase(
    private val repository: ClassFeedbackRepository
) {
    suspend operator fun invoke(
        classId: Long,
        rating: Int,
        comment: String
    ): ApiResult<Feedback> {
        return repository.submitStudentFeedback(classId, rating, comment)
    }
}
