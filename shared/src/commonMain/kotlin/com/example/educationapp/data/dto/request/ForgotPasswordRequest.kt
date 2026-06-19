package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    val username: String
)
