package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentProfileRequest(
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val address: String,
    val zaloLink: String
)
