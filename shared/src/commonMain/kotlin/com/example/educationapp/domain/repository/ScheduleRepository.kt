package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.ScheduleItem
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.entity.AttendanceRecord
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.enums.AttendanceStatus

interface ScheduleRepository {
    suspend fun getMySchedules(fromTime: String, toTime: String): ApiResult<List<ScheduleItem>>
    suspend fun checkIn(teacherId: Long, classId: Long, sessionNumber: Int): ApiResult<TeacherCheckInResult>
    suspend fun getCheckInStatus(classId: Long, sessionNumber: Int, teacherId: Long): ApiResult<TeacherCheckInResult>
    suspend fun checkOut(checkinId: Long): ApiResult<TeacherCheckInResult>
    suspend fun getAttendances(classId: Long, sessionNumber: Int): ApiResult<List<AttendanceRecord>>
    suspend fun submitAttendances(
        classId: Long,
        sessionNumber: Int,
        attendances: List<Triple<Long, AttendanceStatus, String?>>
    ): ApiResult<Unit>
    suspend fun filterClasses(
        search: String?,
        courseId: Long?,
        teacherId: Long?,
        branchId: Long?,
        status: String?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SchoolClass>>
    suspend fun getStudentClasses(
        studentId: Long,
        status: String?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<SchoolClass>>
}


