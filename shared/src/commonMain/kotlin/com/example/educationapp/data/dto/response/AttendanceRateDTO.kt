package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.AttendanceRate
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRateDTO(
    val studentId: Int,
    val classId: Int,
    val attendedSessions: Int,
    val totalSessions: Int,
    val attendanceRate: Double
)

fun AttendanceRateDTO.toDomainEntity() = AttendanceRate(
    studentId = studentId,
    classId = classId,
    attendedSessions = attendedSessions,
    totalSessions = totalSessions,
    attendanceRate = attendanceRate
)
