package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.repository.ScheduleRepository

class TeacherCheckInUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        teacherId: Long,
        classId: Long,
        sessionNumber: Int
    ): ApiResult<TeacherCheckInResult> {
        return repository.checkIn(teacherId, classId, sessionNumber)
    }
}
