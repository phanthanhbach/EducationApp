package com.example.educationapp.domain.entity

data class StudentClassFeedback(
    val classId: Long,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val courseName: String,
    val enrolledDate: String?,
    val status: String,
    val feedbackRating: String?,
    val feedbackComment: String?,
    val feedbackAt: String?,
    val teacherFeedback: String?,
    val teacherFeedbackDate: String?,
    val finalResult: String?,
    val resultNote: String?,
    val resultDate: String?
)
