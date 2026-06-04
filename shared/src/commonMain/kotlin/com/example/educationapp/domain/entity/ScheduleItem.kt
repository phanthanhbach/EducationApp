package com.example.educationapp.domain.entity

data class ScheduleItem(
    val classId: Long,
    val sessionNumber: Int,
    val schoolClassId: Long,
    val className: String,
    val roomId: Long,
    val roomName: String,
    val startTime: String, // raw ISO-8601 format e.g. "2026-06-01T07:00"
    val endTime: String,   // raw ISO-8601 format e.g. "2026-06-01T09:00"
    val notes: String?
)
