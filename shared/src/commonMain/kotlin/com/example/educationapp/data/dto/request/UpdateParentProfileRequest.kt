package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateParentProfileRequest(
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val img: String
)
