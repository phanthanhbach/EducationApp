package com.example.educationapp.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SubmitAttendanceRequest(
    val classId: Long,
    val sessionNumber: Int,
    val attendances: List<AttendanceEntry>
)

@Serializable
data class AttendanceEntry(
    val studentId: Long,
    val status: String,
    val reason: String? = null
)
