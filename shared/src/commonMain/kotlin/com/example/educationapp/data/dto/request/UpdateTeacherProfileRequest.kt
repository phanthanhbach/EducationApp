package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTeacherProfileRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val img: String,
    val teacherCode: String,
    val certificates: List<String>,
    val experience: String
)
