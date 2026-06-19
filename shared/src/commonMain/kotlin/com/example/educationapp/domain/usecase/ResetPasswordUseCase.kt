package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): ApiResult<Unit> {
        return authRepository.resetPassword(
            token = token,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
    }
}
