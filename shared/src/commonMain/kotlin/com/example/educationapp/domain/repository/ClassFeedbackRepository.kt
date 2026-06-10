package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.StudentClassFeedback

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
}
