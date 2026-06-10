package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.ScheduleItem
import com.example.educationapp.domain.repository.ScheduleRepository

class FilterSchedulesNoPaginationUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        classId: Long?,
        roomId: Long? = null,
        fromTime: String? = null,
        toTime: String? = null
    ): ApiResult<List<ScheduleItem>> {
        return repository.filterSchedulesNoPagination(classId, roomId, fromTime, toTime)
    }
}
