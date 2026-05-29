package com.example.educationapp.domain.entity

data class UserToken(
    val accessToken: String,
    val refreshToken: String,
    val userRole: String,
    val fullName: String
)
