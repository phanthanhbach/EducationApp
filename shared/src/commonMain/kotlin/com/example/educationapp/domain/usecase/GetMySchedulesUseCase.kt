package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.ScheduleItem
import com.example.educationapp.domain.repository.ScheduleRepository

class GetMySchedulesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(fromTime: String, toTime: String): ApiResult<List<ScheduleItem>> {
        return repository.getMySchedules(fromTime, toTime)
    }
}
