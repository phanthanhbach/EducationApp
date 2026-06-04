package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.repository.ScheduleRepository

class TeacherCheckOutUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(checkinId: Long): ApiResult<TeacherCheckInResult> {
        return repository.checkOut(checkinId)
    }
}
