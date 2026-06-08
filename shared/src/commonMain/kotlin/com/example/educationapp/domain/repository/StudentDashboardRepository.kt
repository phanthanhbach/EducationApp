package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.domain.entity.AssignmentReminder
import com.example.educationapp.domain.entity.StudentClassInfo

interface StudentDashboardRepository {
    suspend fun getAttendanceRate(studentId: Int, courseClassId: Int): ApiResult<AttendanceRate>
    
    suspend fun getMyCourses(
        search: String?,
        isActive: Boolean?,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<Course>>
    
    suspend fun getAssignmentReminders(
        studentId: Int?,
        classId: Int?,
        dueInHours: Int,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<AssignmentReminder>>
    
    suspend fun getStudentClassesInfo(
        studentId: Long,
        page: Int,
        size: Int
    ): ApiResult<List<StudentClassInfo>>
}
