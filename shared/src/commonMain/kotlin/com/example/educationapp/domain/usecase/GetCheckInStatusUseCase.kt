package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.repository.ScheduleRepository

class GetCheckInStatusUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        classId: Long,
        sessionNumber: Int,
        teacherId: Long
    ): ApiResult<TeacherCheckInResult> {
        return repository.getCheckInStatus(classId, sessionNumber, teacherId)
    }
}
