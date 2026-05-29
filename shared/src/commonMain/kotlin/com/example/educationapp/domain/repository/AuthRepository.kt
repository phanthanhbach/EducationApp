package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserToken

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<UserToken>
    suspend fun logout(): ApiResult<Unit>
}
