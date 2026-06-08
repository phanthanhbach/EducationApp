package com.example.educationapp.domain.entity

data class AssignmentReminder(
    val studentId: Int,
    val studentName: String,
    val assignmentId: Int,
    val title: String,
    val classId: Int,
    val className: String,
    val courseId: Int,
    val courseName: String,
    val dueDate: String,
    val hoursRemaining: Double
)
