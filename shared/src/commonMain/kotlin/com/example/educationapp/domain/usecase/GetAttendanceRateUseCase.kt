package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.repository.StudentDashboardRepository

class GetAttendanceRateUseCase(
    private val repository: StudentDashboardRepository
) {
    suspend operator fun invoke(studentId: Int, courseClassId: Int): ApiResult<AttendanceRate> {
        return repository.getAttendanceRate(studentId, courseClassId)
    }
}
