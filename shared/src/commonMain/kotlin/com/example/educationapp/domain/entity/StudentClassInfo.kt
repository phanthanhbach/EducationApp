package com.example.educationapp.domain.entity

data class StudentClassInfo(
    val classId: Long,
    val className: String,
    val courseName: String,
    val teacherEmail: String?,
    val teacherPhone: String?,
    val status: String
)
