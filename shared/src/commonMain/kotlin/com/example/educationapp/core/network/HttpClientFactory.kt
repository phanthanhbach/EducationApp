package com.example.educationapp.core.network

import com.example.educationapp.core.data.TokenManager
import com.example.educationapp.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import com.example.educationapp.core.data.SessionManager
import com.example.educationapp.data.dto.request.RefreshTokenRequest
import com.example.educationapp.data.dto.response.LoginDTO
import com.example.educationapp.data.endpoint.AuthEndpoint
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

fun createHttpClient(tokenManager: TokenManager, sessionManager: SessionManager, baseUrl: String): HttpClient = HttpClient {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.d(tag = "Network") { message }
            }
        }
    }
    install(Auth) {
        bearer {
            loadTokens {
                val accessToken = tokenManager.getAccessToken() ?: return@loadTokens null
                val cleanToken = if (accessToken.startsWith("Bearer ", ignoreCase = true)) {
                    accessToken.substring(7)
                } else {
                    accessToken
                }
                val refreshToken = tokenManager.getRefreshToken() ?: ""
                BearerTokens(cleanToken, refreshToken)
            }
            refreshTokens {
                val refreshToken = oldTokens?.refreshToken
                if (refreshToken.isNullOrBlank()) {
                    tokenManager.clearTokens()
                    sessionManager.notifySessionExpired()
                    return@refreshTokens null
                }
                try {
                    val response = client.post(AuthEndpoint.REFRESH) {
                        markAsRefreshTokenRequest()
                        setBody(RefreshTokenRequest(refreshToken = refreshToken))
                    }.body<BaseResponse<LoginDTO>>()

                    tokenManager.saveTokens(
                        accessToken = response.data.accessToken,
                        refreshToken = response.data.refreshToken,
                        role = response.data.userRole,
                        fullName = response.data.fullName
                    )
                    BearerTokens(response.data.accessToken, response.data.refreshToken)
                } catch (e: Exception) {
                    tokenManager.clearTokens()
                    sessionManager.notifySessionExpired()
                    null
                }
            }
            sendWithoutRequest { true }
        }
    }
    defaultRequest {
        url(baseUrl)
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

