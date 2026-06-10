package com.example.educationapp.domain.entity

data class StudentAssignment(
    val assignmentId: Int,
    val classId: Int,
    val studentId: Int,
    val studentName: String,
    val title: String,
    val description: String?,
    val dueDate: String,
    val assignmentFileAttachment: String?,
    val finalExam: Boolean,
    val submitted: Boolean,
    val submissionStatus: String?,
    val fileAttachment: String?,
    val submittedAt: String?,
    val score: Double?
)
