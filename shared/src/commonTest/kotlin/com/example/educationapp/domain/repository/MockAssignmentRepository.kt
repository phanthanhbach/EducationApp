package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.entity.AssignmentSubmission
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.domain.entity.SubmissionDetail

class MockAssignmentRepository : AssignmentRepository {
    var shouldReturnError = false
    var lastClassId: Int? = null
    var lastStudentId: Int? = null
    var lastAssignmentId: Int? = null
    var lastScore: Double? = null
    var lastComment: String? = null

    override suspend fun filterAssignments(
        classId: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Assignment>> {
        TODO("Not needed for testing GradeSubmissionUseCase")
    }

    override suspend fun getMyAssignmentsFiltered(
        classId: Int,
        submitted: Boolean,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentAssignment>> {
        TODO("Not needed for testing GradeSubmissionUseCase")
    }

    override suspend fun submitAssignment(
        assignmentId: Int,
        classId: Int,
        studentId: Int,
        file: UploadFile
    ): ApiResult<AssignmentSubmission> {
        TODO("Not needed for testing GradeSubmissionUseCase")
    }

    override suspend fun filterSubmissions(
        assignmentId: Int,
        classId: Int,
        submitted: Boolean?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SubmissionDetail>> {
        TODO("Not needed for testing GradeSubmissionUseCase")
    }

    override suspend fun gradeSubmission(
        classId: Int,
        studentId: Int,
        assignmentId: Int,
        score: Double,
        comment: String
    ): ApiResult<AssignmentSubmission> {
        if (shouldReturnError) {
            return ApiResult.Error.UnknownError(
                message = "Fake repository error",
                exception = Exception("Network failure")
            )
        }

        lastClassId = classId
        lastStudentId = studentId
        lastAssignmentId = assignmentId
        lastScore = score
        lastComment = comment

        return ApiResult.Success(
            AssignmentSubmission(
                classId = classId,
                studentId = studentId,
                assignmentId = assignmentId,
                assignmentTitle = "Mock Assignment Title",
                studentName = "Mock Student Name",
                fileAttachment = "mock-file.pdf",
                submittedAt = "2026-07-17T03:37:11.121Z",
                status = "GRADED",
                score = score,
                teacherComment = comment,
                createdAt = "2026-07-17T03:37:11.121Z"
            )
        )
    }
}
