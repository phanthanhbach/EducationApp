package com.example.educationapp.domain.entity

data class AttendanceRate(
    val studentId: Int,
    val classId: Int,
    val attendedSessions: Int,
    val totalSessions: Int,
    val attendanceRate: Double
)
