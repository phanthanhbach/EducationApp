package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.Course
import kotlinx.serialization.Serializable

@Serializable
data class CourseDTO(
    val courseId: Int,
    val code: String,
    val name: String,
    val level: String,
    val totalSessions: Int,
    val standardPrice: Double,
    val description: String? = null,
    val isActive: Boolean,
    val isDeleted: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun CourseDTO.toDomainEntity() = Course(
    courseId = courseId,
    code = code,
    name = name,
    level = level,
    totalSessions = totalSessions,
    standardPrice = standardPrice,
    description = description,
    isActive = isActive
)
