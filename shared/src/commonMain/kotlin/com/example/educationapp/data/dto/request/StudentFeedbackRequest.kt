package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class StudentFeedbackRequest(
    val rating: Int,
    val comment: String
)
