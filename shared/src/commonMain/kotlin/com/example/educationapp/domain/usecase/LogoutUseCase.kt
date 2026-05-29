package com.example.educationapp.domain.usecase

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): ApiResult<Unit> {
        val result = repository.logout()
        // Dù gọi API logout thành công hay lỗi ở server, ta vẫn nên xóa token cục bộ
        tokenManager.clearTokens()
        return result
    }
}
