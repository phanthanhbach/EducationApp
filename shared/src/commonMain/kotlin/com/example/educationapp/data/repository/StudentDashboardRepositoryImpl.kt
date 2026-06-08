package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.AttendanceRateDTO
import com.example.educationapp.data.dto.response.CourseDTO
import com.example.educationapp.data.dto.response.AssignmentReminderDTO
import com.example.educationapp.data.dto.response.StudentClassDTO
import com.example.educationapp.data.dto.response.toDomainEntity
import com.example.educationapp.data.dto.response.toStudentClassInfo
import com.example.educationapp.data.endpoint.AttendanceEndpoint
import com.example.educationapp.data.endpoint.CourseEndpoint
import com.example.educationapp.data.endpoint.AssignmentEndpoint
import com.example.educationapp.data.endpoint.ClassEndpoint
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.StudentClassInfo
import com.example.educationapp.domain.repository.StudentDashboardRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class StudentDashboardRepositoryImpl(
    private val httpClient: HttpClient
) : StudentDashboardRepository {

    override suspend fun getAttendanceRate(studentId: Int, courseClassId: Int): ApiResult<AttendanceRate> {
        return safeApiCall {
            val response = httpClient.get(AttendanceEndpoint.RATE) {
                parameter("studentId", studentId)
                parameter("courseClassId", courseClassId)
            }.body<BaseResponse<AttendanceRateDTO>>()
            response.data.toDomainEntity()
        }
    }

    override suspend fun getMyCourses(
        search: String?,
        isActive: Boolean?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Course>> {
        return safeApiCall {
            val response = httpClient.get(CourseEndpoint.ME) {
                parameter("search", search)
                parameter("is_active", isActive)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<CourseDTO>>>()

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

    override suspend fun getAssignmentReminders(
        studentId: Int?,
        classId: Int?,
        dueInHours: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<AssignmentReminder>> {
        return safeApiCall {
            val response = httpClient.get(AssignmentEndpoint.MY_REMINDERS) {
                parameter("studentId", studentId)
                parameter("classId", classId)
                parameter("dueInHours", dueInHours)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<AssignmentReminderDTO>>>()

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

    override suspend fun getStudentClassesInfo(
        studentId: Long,
        page: Int,
        size: Int
    ): ApiResult<List<StudentClassInfo>> {
        return safeApiCall {
            val response = httpClient.get(ClassEndpoint.studentClasses(studentId)) {
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<StudentClassDTO>>>()
            response.data.content.map { it.toStudentClassInfo() }
        }
    }
}
