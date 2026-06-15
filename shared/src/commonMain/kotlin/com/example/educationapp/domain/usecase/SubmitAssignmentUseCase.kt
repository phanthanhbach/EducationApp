package com.example.educationapp.domain.usecase

import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.AssignmentSubmission
import com.example.educationapp.domain.repository.AssignmentRepository

class SubmitAssignmentUseCase(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(
        assignmentId: Int,
        classId: Int,
        studentId: Int,
        file: UploadFile
    ): ApiResult<AssignmentSubmission> {
        return repository.submitAssignment(
            assignmentId = assignmentId,
            classId = classId,
            studentId = studentId,
            file = file
        )
    }
}

