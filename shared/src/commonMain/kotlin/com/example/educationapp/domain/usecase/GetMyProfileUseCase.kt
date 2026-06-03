package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.repository.ProfileRepository

class GetMyProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(role: AppRole): ApiResult<UserProfile> {
        return repository.getMyProfile(role)
    }
}
