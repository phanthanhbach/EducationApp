package com.example.educationapp.domain.entity

import com.example.educationapp.domain.enums.AttendanceStatus

data class AttendanceRecord(
    val classId: Long,
    val sessionNumber: Int,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val sessionStart: String?,
    val sessionEnd: String?,
    val status: AttendanceStatus?,
    val checkedInAt: String?,
    val note: String?,
    val createdAt: String?,
    val updatedAt: String?
)
