package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.data.dto.request.SubmitAssignmentRequest
import com.example.educationapp.data.dto.response.AssignmentSubmissionDTO
import com.example.educationapp.data.dto.response.AssignmentDTO
import com.example.educationapp.data.dto.response.StudentAssignmentDTO
import com.example.educationapp.data.dto.response.toDomainEntity
import com.example.educationapp.data.endpoint.AssignmentEndpoint
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.domain.entity.AssignmentSubmission
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.domain.repository.AssignmentRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AssignmentRepositoryImpl(
    private val httpClient: HttpClient,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
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

    override suspend fun getMyAssignmentsFiltered(
        classId: Int,
        submitted: Boolean,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<StudentAssignment>> {
        return safeApiCall {
            val response = httpClient.get(AssignmentEndpoint.MY_FILTER) {
                parameter("classId", classId)
                parameter("submitted", submitted)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<StudentAssignmentDTO>>>()

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

    override suspend fun submitAssignment(
        assignmentId: Int,
        classId: Int,
        studentId: Int,
        file: UploadFile
    ): ApiResult<AssignmentSubmission> {
        return safeApiCall {
            val data = json.encodeToString(
                SubmitAssignmentRequest(
                    classId = classId,
                    studentId = studentId
                )
            )

            val response = httpClient.post(AssignmentEndpoint.submit(assignmentId)) {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "data",
                                value = data,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                }
                            )
                            append(
                                key = "file",
                                value = file.bytes,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${file.name.sanitizeFileName()}\""
                                    )
                                    append(
                                        HttpHeaders.ContentType,
                                        (file.mimeType ?: ContentType.Application.OctetStream.toString())
                                    )
                                }
                            )
                        }
                    )
                )
            }.body<BaseResponse<AssignmentSubmissionDTO>>()

            response.data.toDomainEntity()
        }
    }
}

private fun String.sanitizeFileName(): String {
    return replace("\"", "")
        .replace("\r", "")
        .replace("\n", "")
        .ifBlank { "upload-file" }
}
