package com.example.educationapp.domain.entity

data class SchoolClass(
    val id: Long,
    val name: String,
    val courseId: Long,
    val courseName: String,
    val teacherId: Long,
    val teacherName: String,
    val branchId: Long,
    val branchName: String,
    val maxStudents: Int,
    val currentStudents: Int,
    val status: String,
    val startDate: String,
    val endDate: String,
    val zaloGroupLink: String?,
    val zaloGroupName: String?,
    val finalResult: String? = null
)
