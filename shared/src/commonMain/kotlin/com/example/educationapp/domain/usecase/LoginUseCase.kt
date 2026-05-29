package com.example.educationapp.domain.usecase

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserToken
import com.example.educationapp.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(username: String, password: String): ApiResult<UserToken> {
        val result = repository.login(username, password)
        if (result is ApiResult.Success) {
            tokenManager.saveTokens(
                accessToken = result.data.accessToken,
                refreshToken = result.data.refreshToken
            )
        }
        return result
    }
}
