package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserInfo

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<UserInfo>
    suspend fun logout(): ApiResult<Unit>
}
