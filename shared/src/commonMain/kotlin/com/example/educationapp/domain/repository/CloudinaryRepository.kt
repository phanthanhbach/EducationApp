package com.example.educationapp.domain.repository

import com.example.educationapp.core.network.ApiResult

interface CloudinaryRepository {
    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): ApiResult<String>
}
