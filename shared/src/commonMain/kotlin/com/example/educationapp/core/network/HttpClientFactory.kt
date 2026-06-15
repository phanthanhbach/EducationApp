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
import kotlinx.serialization.json.Json
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
        url("http://cnxvn.ddns.net:9000/api/v1/")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

