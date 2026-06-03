package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole

interface ProfileRepository {
    suspend fun getMyProfile(role: AppRole): ApiResult<UserProfile>
}
