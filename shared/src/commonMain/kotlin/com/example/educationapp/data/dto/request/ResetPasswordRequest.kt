package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
)
