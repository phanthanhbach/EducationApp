package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserInfo

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<UserInfo>
    suspend fun logout(): ApiResult<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String): ApiResult<Unit>
    suspend fun forgotPassword(username: String): ApiResult<String>
    suspend fun resetPassword(token: String, newPassword: String, confirmPassword: String): ApiResult<Unit>
}
