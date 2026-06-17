package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.PaginationResponse
import com.example.educationapp.domain.entity.TeacherCheckInResult
import com.example.educationapp.domain.repository.ScheduleRepository

class GetTeacherCheckInsUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        teacherId: Long,
        page: Int,
        size: Int
    ): ApiResult<PaginationResponse<TeacherCheckInResult>> {
        return repository.getTeacherCheckIns(teacherId, page, size)
    }
}
