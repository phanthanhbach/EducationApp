package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TeacherDTO(
    val teacherId: Int,
    val teacherCode: String? = null,
    val fullName: String,
    val email: String? = null,
    val phone: String? = null,
    val img: String? = null,
    val certificates: List<String>? = null,
    val hourlyRate: Double? = null,
    val experience: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
