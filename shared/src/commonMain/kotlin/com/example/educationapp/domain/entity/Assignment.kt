package com.example.educationapp.domain.entity

data class Assignment(
    val id: Int,
    val title: String,
    val description: String?,
    val classId: Int,
    val className: String,
    val courseId: Int,
    val courseName: String,
    val fileAttachment: String?,
    val dueDate: String,
    val active: Boolean,
    val finalExam: Boolean,
    val submittedCount: Int,
    val notSubmittedCount: Int
)
