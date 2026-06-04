package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.AttendanceRecord
import com.example.educationapp.domain.repository.ScheduleRepository

class GetAttendancesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(classId: Long, sessionNumber: Int): ApiResult<List<AttendanceRecord>> {
        return repository.getAttendances(classId, sessionNumber)
    }
}
