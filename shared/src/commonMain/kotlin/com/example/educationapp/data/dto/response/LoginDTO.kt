package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: String,
    val refreshTokenExpiresAt: String,
    val userRole: String,
    val fullName: String
)
