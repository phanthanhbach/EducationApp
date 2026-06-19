package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.AuthRepository

class ForgotPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String): ApiResult<String> {
        return authRepository.forgotPassword(username)
    }
}
