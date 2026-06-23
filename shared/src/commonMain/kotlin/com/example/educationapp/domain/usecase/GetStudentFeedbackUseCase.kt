package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.repository.ScheduleRepository

class GetStudentFeedbackUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        studentId: Long,
        classId: Long
    ): ApiResult<StudentClassFeedback?> {
        return repository.getStudentFeedbackByClass(studentId, classId)
    }
}
