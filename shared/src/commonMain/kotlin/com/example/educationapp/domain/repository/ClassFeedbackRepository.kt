package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.entity.Feedback

interface ClassFeedbackRepository {
    suspend fun getClassFeedbacks(
        classId: Long,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentClassFeedback>>

    suspend fun submitTeacherFeedback(
        classId: Long,
        studentId: Long,
        teacherFeedback: String
    ): ApiResult<StudentClassFeedback>

    suspend fun getFeedbackNoPagination(
        classId: Long,
        studentId: Long,
        feedbackType: String
    ): ApiResult<Feedback?>
}
