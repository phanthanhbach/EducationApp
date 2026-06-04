package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.TeacherCheckInResult
import kotlinx.serialization.Serializable

@Serializable
data class TeacherCheckInResponseDTO(
    val checkinId: Long,
    val teacherId: Long,
    val teacherName: String? = null,
    val classId: Long,
    val className: String? = null,
    val sessionNumber: Int,
    val status: String? = null,
    val checkedOut: Boolean? = null,
    val lateMinutes: Int? = null,
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun TeacherCheckInResponseDTO.toDomainEntity() = TeacherCheckInResult(
    checkinId = checkinId,
    teacherId = teacherId,
    teacherName = teacherName,
    classId = classId,
    className = className,
    sessionNumber = sessionNumber,
    status = status ?: "PENDING",
    checkedOut = checkedOut,
    lateMinutes = lateMinutes,
    checkinTime = checkinTime,
    checkoutTime = checkoutTime,
    createdAt = createdAt,
    updatedAt = updatedAt
)

