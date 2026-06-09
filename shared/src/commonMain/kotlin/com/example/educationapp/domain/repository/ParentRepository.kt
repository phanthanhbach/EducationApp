package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile

interface ParentRepository {
    suspend fun getMyChildren(): ApiResult<List<UserProfile.Student>>
}
