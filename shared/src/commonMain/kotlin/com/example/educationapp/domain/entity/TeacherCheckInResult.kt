package com.example.educationapp.domain.entity

import com.example.educationapp.domain.enums.CheckInStatus

data class TeacherCheckInResult(
    val checkinId: Long,
    val teacherId: Long,
    val teacherName: String? = null,
    val classId: Long,
    val className: String? = null,
    val sessionNumber: Int,
    val status: CheckInStatus? = null,
    val checkedOut: Boolean? = null,
    val lateMinutes: Int? = null,
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
