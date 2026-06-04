package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.AttendanceRecord
import com.example.educationapp.domain.enums.AttendanceStatus
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRecordDTO(
    val classId: Long,
    val sessionNumber: Int,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val sessionStart: String? = null,
    val sessionEnd: String? = null,
    val status: String? = null,
    val checkedInAt: String? = null,
    val note: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun AttendanceRecordDTO.toDomainEntity(): AttendanceRecord {
    val parsedStatus = try {
        status?.let { AttendanceStatus.valueOf(it) }
    } catch (e: Exception) {
        null
    }
    return AttendanceRecord(
        classId = classId,
        sessionNumber = sessionNumber,
        studentId = studentId,
        studentName = studentName,
        className = className,
        sessionStart = sessionStart,
        sessionEnd = sessionEnd,
        status = parsedStatus,
        checkedInAt = checkedInAt,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
