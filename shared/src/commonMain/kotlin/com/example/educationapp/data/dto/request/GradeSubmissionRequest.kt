package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class GradeSubmissionRequest(
    val score: Double,
    val comment: String
)
