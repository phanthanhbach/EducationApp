package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.TeacherRatingSummary
import kotlinx.serialization.Serializable

@Serializable
data class TeacherRatingSummaryDTO(
    val teacherId: Long,
    val teacherName: String,
    val totalFeedback: Int,
    val totalRatings: Int,
    val averageRating: Double
)

fun TeacherRatingSummaryDTO.toDomainEntity() = TeacherRatingSummary(
    teacherId = teacherId,
    teacherName = teacherName,
    totalFeedback = totalFeedback,
    totalRatings = totalRatings,
    averageRating = averageRating
)
