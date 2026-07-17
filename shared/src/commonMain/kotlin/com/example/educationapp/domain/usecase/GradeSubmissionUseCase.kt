package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.AssignmentSubmission
import com.example.educationapp.domain.repository.AssignmentRepository

class GradeSubmissionUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(
        classId: Int,
        studentId: Int,
        assignmentId: Int,
        score: Double,
        comment: String
    ): ApiResult<AssignmentSubmission> {
        return repository.gradeSubmission(
            classId = classId,
            studentId = studentId,
            assignmentId = assignmentId,
            score = score,
            comment = comment
        )
    }
}
