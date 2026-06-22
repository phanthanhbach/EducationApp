package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.CloudinaryRepository

class UploadAvatarUseCase(
    private val repository: CloudinaryRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        fileName: String = "avatar.jpg"
    ): ApiResult<String> {
        return repository.uploadAvatar(imageBytes, fileName)
    }
}
