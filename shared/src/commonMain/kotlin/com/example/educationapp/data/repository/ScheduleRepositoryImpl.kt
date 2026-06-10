package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.core.network.safePaginatedApiCall
import com.example.educationapp.data.dto.request.TeacherCheckInRequest
import com.example.educationapp.data.dto.request.SubmitAttendanceRequest
import com.example.educationapp.data.dto.request.AttendanceEntry
import com.example.educationapp.data.dto.response.ScheduleItemDTO
import com.example.educationapp.data.dto.response.TeacherCheckInResponseDTO
import com.example.educationapp.data.dto.response.AttendanceRecordDTO
import com.example.educationapp.data.dto.response.toDomainEntity
import com.example.educationapp.data.dto.response.toScheduleItemEntity
import com.example.educationapp.data.endpoint.ScheduleEndpoint
import com.example.educationapp.data.endpoint.TeacherCheckInEndpoint
import com.example.educationapp.data.endpoint.AttendanceEndpoint
import com.example.educationapp.domain.entity.ScheduleItem
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.entity.AttendanceRecord
import com.example.educationapp.domain.enums.AttendanceStatus
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.data.dto.response.SchoolClassDTO
import com.example.educationapp.data.dto.response.StudentClassDTO
import com.example.educationapp.data.dto.response.toSchoolClass
import com.example.educationapp.data.endpoint.ClassEndpoint
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.repository.ScheduleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody

class ScheduleRepositoryImpl(
    private val httpClient: HttpClient
) : ScheduleRepository {

    override suspend fun getMySchedules(fromTime: String, toTime: String): ApiResult<List<ScheduleItem>> {
        return safePaginatedApiCall<ScheduleItemDTO, ScheduleItem>(
            fetchPage = { page ->
                httpClient.get(ScheduleEndpoint.ME) {
                    parameter("fromTime", fromTime)
                    parameter("toTime", toTime)
                    parameter("page", page)
                    parameter("size", 50)
                }.body()
            },
            mapDto = { dto -> dto.toScheduleItemEntity() }
        )
    }

    override suspend fun checkIn(
        teacherId: Long,
        classId: Long,
        sessionNumber: Int
    ): ApiResult<TeacherCheckInResult> {
        return safeApiCall {
            val response = httpClient.post(TeacherCheckInEndpoint.CHECK_IN) {
                setBody(TeacherCheckInRequest(teacherId = teacherId, classId = classId, sessionNumber = sessionNumber))
            }.body<BaseResponse<TeacherCheckInResponseDTO>>()

            response.data.toDomainEntity()
        }
    }

    override suspend fun getCheckInStatus(
        classId: Long,
        sessionNumber: Int,
        teacherId: Long
    ): ApiResult<TeacherCheckInResult> {
        return safeApiCall {
            val response = httpClient.get(TeacherCheckInEndpoint.SESSION) {
                parameter("classId", classId)
                parameter("sessionNumber", sessionNumber)
                parameter("teacherId", teacherId)
            }.body<BaseResponse<TeacherCheckInResponseDTO>>()

            response.data.toDomainEntity()
        }
    }

    override suspend fun getAttendances(classId: Long, sessionNumber: Int): ApiResult<List<AttendanceRecord>> {
        return safePaginatedApiCall<AttendanceRecordDTO, AttendanceRecord>(
            fetchPage = { page ->
                httpClient.get(AttendanceEndpoint.getScheduleAttendances(classId, sessionNumber)) {
                    parameter("page", page)
                    parameter("size", 100)
                }.body()
            },
            mapDto = { dto -> dto.toDomainEntity() }
        )
    }

    override suspend fun submitAttendances(
        classId: Long,
        sessionNumber: Int,
        attendances: List<Triple<Long, AttendanceStatus, String?>>
    ): ApiResult<Unit> {
        return safeApiCall {
            httpClient.post(AttendanceEndpoint.SUBMIT) {
                setBody(
                    SubmitAttendanceRequest(
                        classId = classId,
                        sessionNumber = sessionNumber,
                        attendances = attendances.map { (studentId, status, reason) ->
                            AttendanceEntry(
                                studentId = studentId,
                                status = status.name,
                                reason = reason
                            )
                        }
                    )
                )
            }.body<Unit>()
        }
    }

    override suspend fun checkOut(checkinId: Long): ApiResult<TeacherCheckInResult> {
        return safeApiCall {
            val response = httpClient.post(TeacherCheckInEndpoint.checkOut(checkinId)).body<BaseResponse<TeacherCheckInResponseDTO>>()
            response.data.toDomainEntity()
        }
    }

    override suspend fun filterClasses(
        search: String?,
        courseId: Long?,
        teacherId: Long?,
        branchId: Long?,
        status: String?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SchoolClass>> {
        return safeApiCall {
            val response = httpClient.get(ClassEndpoint.FILTER) {
                parameter("search", search)
                parameter("courseId", courseId)
                parameter("teacherId", teacherId)
                parameter("branchId", branchId)
                parameter("status", status)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<SchoolClassDTO>>>()

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

    override suspend fun getStudentClasses(
        studentId: Long,
        status: String?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SchoolClass>> {
        return safeApiCall {
            val response = httpClient.get(ClassEndpoint.studentClasses(studentId)) {
                parameter("status", status)
                parameter("page", page)
                parameter("size", size)
            }.body<BaseResponse<PaginationResponse<StudentClassDTO>>>()

            val paginatedData = response.data
            PaginationResponse(
                content = paginatedData.content.map { it.toSchoolClass() },
                number = paginatedData.number,
                size = paginatedData.size,
                totalElements = paginatedData.totalElements,
                totalPages = paginatedData.totalPages,
                last = paginatedData.last,
                first = paginatedData.first
            )
        }
    }

    override suspend fun getStudentClassesNoPagination(
        studentId: Long,
        status: String?
    ): ApiResult<List<SchoolClass>> {
        return safePaginatedApiCall<StudentClassDTO, SchoolClass>(
            fetchPage = { page ->
                httpClient.get(ClassEndpoint.studentClasses(studentId)) {
                    parameter("status", status)
                    parameter("page", page)
                    parameter("size", 20)
                }.body()
            },
            mapDto = { dto -> dto.toSchoolClass() }
        )
    }

    override suspend fun filterSchedulesNoPagination(
        classId: Long?,
        roomId: Long?,
        fromTime: String?,
        toTime: String?
    ): ApiResult<List<ScheduleItem>> {
        return safePaginatedApiCall<ScheduleItemDTO, ScheduleItem>(
            fetchPage = { page ->
                httpClient.get(ScheduleEndpoint.FILTER) {
                    parameter("classId", classId)
                    parameter("roomId", roomId)
                    parameter("fromTime", fromTime)
                    parameter("toTime", toTime)
                    parameter("page", page)
                    parameter("size", 50)
                }.body()
            },
            mapDto = { dto -> dto.toScheduleItemEntity() }
        )
    }
}


