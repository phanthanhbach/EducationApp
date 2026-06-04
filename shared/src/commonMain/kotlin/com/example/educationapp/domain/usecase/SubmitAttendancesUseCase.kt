package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.enums.AttendanceStatus
import com.example.educationapp.domain.repository.ScheduleRepository

class SubmitAttendancesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        classId: Long,
        sessionNumber: Int,
        attendances: List<Triple<Long, AttendanceStatus, String?>>
    ): ApiResult<Unit> {
        return repository.submitAttendances(classId, sessionNumber, attendances)
    }
}
