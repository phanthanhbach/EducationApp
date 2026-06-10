package com.example.educationapp.domain.entity

data class Feedback(
    val id: Long,
    val feedbackType: String,
    val studentId: Long,
    val studentName: String,
    val teacherId: Long,
    val teacherName: String,
    val classId: Long,
    val className: String,
    val feedbackRating: Int,
    val feedbackComment: String?,
    val feedbackAt: String?,
    val teacherFeedback: String?,
    val teacherFeedbackDate: String?
)
