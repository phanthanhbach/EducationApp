package com.example.educationapp.core.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.example.educationapp.core.data.TokenManager
import co.touchlab.kermit.Logger as KermitLogger

fun createHttpClient(tokenManager: TokenManager): HttpClient = HttpClient {
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
                tokenManager.getAccessToken()?.let { token ->
                    val cleanToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
                        token.substring(7)
                    } else {
                        token
                    }
                    BearerTokens(cleanToken, "")
                }
            }
        }
    }
    defaultRequest {
        url("http://10.11.11.211:8081/api/v1/") // Thay đổi thành API URL của bạn sau này
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

