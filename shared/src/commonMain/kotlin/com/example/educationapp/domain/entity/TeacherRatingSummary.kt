package com.example.educationapp.domain.entity

data class TeacherRatingSummary(
    val teacherId: Long,
    val teacherName: String,
    val totalFeedback: Int,
    val totalRatings: Int,
    val averageRating: Double
)
