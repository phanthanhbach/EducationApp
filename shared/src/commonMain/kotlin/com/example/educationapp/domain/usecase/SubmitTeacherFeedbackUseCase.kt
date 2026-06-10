package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository

class SubmitTeacherFeedbackUseCase(
    private val repository: ClassFeedbackRepository
) {
    suspend operator fun invoke(
        classId: Long,
        studentId: Long,
        teacherFeedback: String
    ): ApiResult<StudentClassFeedback> {
        return repository.submitTeacherFeedback(classId, studentId, teacherFeedback)
    }
}
