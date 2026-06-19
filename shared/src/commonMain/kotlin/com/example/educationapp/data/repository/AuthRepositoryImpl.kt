package com.example.educationapp.data.repository

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.request.LoginRequest
import com.example.educationapp.data.dto.request.ChangePasswordRequest
import com.example.educationapp.data.dto.response.LoginDTO
import com.example.educationapp.data.endpoint.AuthEndpoint
import com.example.educationapp.domain.entity.UserInfo
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.post
import io.ktor.client.request.setBody


class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): ApiResult<UserInfo> {
        return safeApiCall {
            val response = httpClient.post(AuthEndpoint.LOGIN) {
                setBody(LoginRequest(username = username, password = password))
            }.body<BaseResponse<LoginDTO>>()

            tokenManager.saveTokens(
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken,
                role = response.data.userRole,
                fullName = response.data.fullName
            )

            // Clear Ktor Bearer Token cache so that it reloads from TokenManager
            clearKtorAuthCache()

            UserInfo(
                userRole = AppRole.fromString(response.data.userRole),
                fullName = response.data.fullName
            )
        }
    }

    override suspend fun logout(): ApiResult<Unit> {
        return safeApiCall {
            httpClient.post(AuthEndpoint.LOGOUT)
            tokenManager.clearTokens()

            // Clear Ktor Bearer Token cache
            clearKtorAuthCache()

            Unit
        }
    }

    private fun clearKtorAuthCache() {
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ApiResult<Unit> {
        return safeApiCall {
            httpClient.post(AuthEndpoint.CHANGE_PASSWORD) {
                setBody(
                    ChangePasswordRequest(
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        confirmPassword = confirmPassword
                    )
                )
            }
            Unit
        }
    }
}


