package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TeacherCheckInRequest(
    val teacherId: Long,
    val classId: Long,
    val sessionNumber: Int
)
