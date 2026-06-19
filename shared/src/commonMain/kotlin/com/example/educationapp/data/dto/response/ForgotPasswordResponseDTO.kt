package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordResponseDTO(
    val expiresAt: String
)
