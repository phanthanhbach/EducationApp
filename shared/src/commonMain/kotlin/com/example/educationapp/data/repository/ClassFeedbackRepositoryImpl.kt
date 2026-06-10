package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.request.TeacherFeedbackRequest
import com.example.educationapp.data.dto.response.StudentClassDTO
import com.example.educationapp.data.dto.response.TeacherFeedbackDTO
import com.example.educationapp.data.dto.response.toStudentClassFeedback
import com.example.educationapp.data.endpoint.ClassEndpoint
import com.example.educationapp.data.endpoint.FeedbackEndpoint
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository
import com.example.educationapp.data.dto.response.FeedbackDTO
import com.example.educationapp.data.dto.response.toDomainEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ClassFeedbackRepositoryImpl(
    private val httpClient: HttpClient
) : ClassFeedbackRepository {

    override suspend fun getClassFeedbacks(
        classId: Long,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentClassFeedback>> {
        return safeApiCall {
            val response = httpClient.get(ClassEndpoint.studentClassesByClass(classId)) {
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<StudentClassDTO>>>()

            val paginatedData = response.data
            PaginationResponse(
                content = paginatedData.content.map { it.toStudentClassFeedback() },
                number = paginatedData.number,
                size = paginatedData.size,
                totalElements = paginatedData.totalElements,
                totalPages = paginatedData.totalPages,
                last = paginatedData.last,
                first = paginatedData.first
            )
        }
    }

    override suspend fun submitTeacherFeedback(
        classId: Long,
        studentId: Long,
        teacherFeedback: String
    ): ApiResult<StudentClassFeedback> {
        return safeApiCall {
            val response = httpClient.post(
                FeedbackEndpoint.teacherClassStudentFeedback(classId, studentId)
            ) {
                setBody(TeacherFeedbackRequest(teacherFeedback = teacherFeedback))
            }.body<BaseResponse<TeacherFeedbackDTO>>()

            response.data.toStudentClassFeedback()
        }
    }

    override suspend fun getFeedbackNoPagination(
        classId: Long,
        studentId: Long,
        feedbackType: String
    ): ApiResult<Feedback?> {
        return safeApiCall {
            val response = httpClient.get(FeedbackEndpoint.FILTER) {
                parameter("classId", classId)
                parameter("studentId", studentId)
                parameter("feedbackType", feedbackType)
            }.body<BaseResponse<PaginationResponse<FeedbackDTO>>>()

            response.data.content.firstOrNull()?.toDomainEntity()
        }
    }
}

private fun TeacherFeedbackDTO.toStudentClassFeedback() = StudentClassFeedback(
    classId = classId,
    studentId = studentId,
    studentName = studentName.orEmpty(),
    className = className.orEmpty(),
    courseName = "",
    enrolledDate = null,
    status = status ?: classStudentStatus.orEmpty(),
    feedbackRating = feedbackRating?.toString(),
    feedbackComment = feedbackComment,
    feedbackAt = feedbackAt,
    teacherFeedback = teacherFeedback,
    teacherFeedbackDate = teacherFeedbackDate,
    finalResult = null,
    resultNote = null,
    resultDate = null
)
