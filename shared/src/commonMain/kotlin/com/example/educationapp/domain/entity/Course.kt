package com.example.educationapp.domain.entity

data class Course(
    val courseId: Int,
    val code: String,
    val name: String,
    val level: String,
    val totalSessions: Int,
    val standardPrice: Double,
    val description: String?,
    val isActive: Boolean
)
