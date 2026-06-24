package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.domain.entity.StudentClassFeedback

class MockClassFeedbackRepository : ClassFeedbackRepository {
    var shouldReturnError = false
    var lastClassId: Long? = null
    var lastRating: Int? = null
    var lastComment: String? = null

    override suspend fun submitStudentFeedback(
        classId: Long,
        rating: Int,
        comment: String
    ): ApiResult<Feedback> {
        if (shouldReturnError) {
            return ApiResult.Error.UnknownError(
                message = "Fake repository error",
                exception = Exception("Network failure")
            )
        }
        
        lastClassId = classId
        lastRating = rating
        lastComment = comment

        return ApiResult.Success(
            Feedback(
                id = 100L,
                feedbackType = "STUDENT",
                studentId = 1L,
                studentName = "Test Student",
                teacherId = 2L,
                teacherName = "Test Teacher",
                classId = classId,
                className = "Test Class",
                feedbackRating = rating,
                feedbackComment = comment,
                feedbackAt = "2026-06-24T12:00:00Z",
                teacherFeedback = null,
                teacherFeedbackDate = null
            )
        )
    }

    override suspend fun getClassFeedbacks(
        classId: Long,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentClassFeedback>> {
        TODO("Not needed for testing SubmitStudentFeedbackUseCase")
    }

    override suspend fun submitTeacherFeedback(
        classId: Long,
        studentId: Long,
        teacherFeedback: String
    ): ApiResult<StudentClassFeedback> {
        TODO("Not needed for testing SubmitStudentFeedbackUseCase")
    }

    override suspend fun getFeedbackNoPagination(
        classId: Long,
        studentId: Long,
        feedbackType: String
    ): ApiResult<Feedback?> {
        TODO("Not needed for testing SubmitStudentFeedbackUseCase")
    }
}
