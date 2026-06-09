package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.AssignmentDTO
import com.example.educationapp.data.dto.response.toDomainEntity
import com.example.educationapp.data.endpoint.AssignmentEndpoint
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.repository.AssignmentRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AssignmentRepositoryImpl(
    private val httpClient: HttpClient
) : AssignmentRepository {

    override suspend fun filterAssignments(
        classId: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Assignment>> {
        return safeApiCall {
            val response = httpClient.get(AssignmentEndpoint.FILTER) {
                parameter("classId", classId)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<AssignmentDTO>>>()

            val paginatedData = response.data
            PaginationResponse(
                content = paginatedData.content.map { it.toDomainEntity() },
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
