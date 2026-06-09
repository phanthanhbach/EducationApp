package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.StudentClassDTO
import com.example.educationapp.data.dto.response.toStudentClassFeedback
import com.example.educationapp.data.endpoint.ClassEndpoint
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.domain.repository.ClassFeedbackRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

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
}
