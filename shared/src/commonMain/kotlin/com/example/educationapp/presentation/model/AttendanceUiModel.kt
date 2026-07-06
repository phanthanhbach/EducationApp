package com.example.educationapp.presentation.model

import com.example.educationapp.domain.enums.AttendanceStatus

data class AttendanceUiModel(
    val studentId: Long,
    val studentName: String,
    val originalStatus: AttendanceStatus?, // Null if not marked before
    val status: AttendanceStatus, // Default is PRESENT
    val reason: String? = null
)