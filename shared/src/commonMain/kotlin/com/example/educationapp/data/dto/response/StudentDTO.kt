package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StudentDTO(
    val studentId: Int,
    val studentCode: String? = null,
    val fullName: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val img: String? = null,
    val zaloLink: String? = null,
    val currentLevel: String? = null,
    val status: String? = null,
    val parentId: Int? = null,
    val parentName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
