package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.TeacherRatingSummary
import com.example.educationapp.domain.repository.ProfileRepository

class GetTeacherRatingSummaryUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(teacherId: Long): ApiResult<TeacherRatingSummary> {
        return repository.getTeacherRatingSummary(teacherId)
    }
}
