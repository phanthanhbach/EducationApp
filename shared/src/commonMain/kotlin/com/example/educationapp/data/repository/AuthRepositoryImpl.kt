package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.BaseResponse
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.request.LoginRequest
import com.example.educationapp.data.dto.response.LoginResponse
import com.example.educationapp.domain.entity.UserToken
import com.example.educationapp.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun login(username: String, password: String): ApiResult<UserToken> {
        return safeApiCall {
            val response = httpClient.post("auth/login") {
                setBody(LoginRequest(username = username, password = password))
            }.body<BaseResponse<LoginResponse>>()

            UserToken(
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken,
                userRole = response.data.userRole,
                fullName = response.data.fullName
            )
        }
    }

    override suspend fun logout(): ApiResult<Unit> {
        return safeApiCall {
            httpClient.post("auth/logout")
            Unit
        }
    }
}
