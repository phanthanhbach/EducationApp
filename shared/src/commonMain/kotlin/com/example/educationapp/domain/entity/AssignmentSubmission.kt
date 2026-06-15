package com.example.educationapp.domain.entity

data class AssignmentSubmission(
    val classId: Int,
    val studentId: Int,
    val assignmentId: Int,
    val assignmentTitle: String,
    val studentName: String,
    val fileAttachment: String?,
    val submittedAt: String?,
    val status: String?,
    val score: Double?,
    val teacherComment: String?,
    val createdAt: String?
)

