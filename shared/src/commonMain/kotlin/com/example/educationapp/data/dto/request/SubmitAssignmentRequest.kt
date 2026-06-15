package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAssignmentRequest(
    val classId: Int,
    val studentId: Int
)

