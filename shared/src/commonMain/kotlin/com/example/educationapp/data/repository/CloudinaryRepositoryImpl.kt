package com.example.educationapp.data.repository

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.network.CloudinaryConfig
import com.example.educationapp.core.network.safeApiCall
import com.example.educationapp.data.dto.response.CloudinaryUploadResponse
import com.example.educationapp.domain.repository.CloudinaryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

class CloudinaryRepositoryImpl : CloudinaryRepository {

    /**
     * Dedicated HttpClient for Cloudinary uploads.
     * Separate from the app's main HttpClient because:
     * - No Bearer auth needed (unsigned upload)
     * - Uses multipart/form-data instead of JSON
     * - Different base URL
     */
    private val cloudinaryClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d(tag = "Cloudinary") { message }
                }
            }
        }
    }

    override suspend fun uploadAvatar(
        imageBytes: ByteArray,
        fileName: String
    ): ApiResult<String> {
        if (imageBytes.size > CloudinaryConfig.MAX_AVATAR_SIZE_BYTES) {
            return ApiResult.Error.UnknownError(
                message = "Image size exceeds ${CloudinaryConfig.MAX_AVATAR_SIZE_BYTES / (1024 * 1024)}MB limit.",
                exception = IllegalArgumentException("File too large")
            )
        }

        return safeApiCall {
            val response = cloudinaryClient.submitFormWithBinaryData(
                url = CloudinaryConfig.CLOUDINARY_UPLOAD_URL,
                formData = formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                    append("upload_preset", CloudinaryConfig.CLOUDINARY_UPLOAD_PRESET)
                }
            ).body<CloudinaryUploadResponse>()

            response.secureUrl
        }
    }
}
