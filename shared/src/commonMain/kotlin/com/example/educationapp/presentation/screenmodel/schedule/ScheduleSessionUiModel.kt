package com.example.educationapp.presentation.screenmodel.schedule

import com.example.educationapp.domain.enums.SessionStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Instant

data class ScheduleSessionUiModel(
    val id: String,
    val classId: Long,
    val sessionNumber: Int,
    val subjectName: String,
    val className: String,
    val room: String,
    val startTimeRaw: String, // e.g. "2026-06-01T07:00"
    val endTimeRaw: String,   // e.g. "2026-06-01T09:00"
    val attendanceText: String,
    val date: LocalDate
) {
    val startTimeFormatted: String by lazy {
        try {
            val localDateTime = LocalDateTime.parse(startTimeRaw)
            "${localDateTime.hour.toString().padStart(2, '0')}:${
                localDateTime.minute.toString().padStart(2, '0')
            }"
        } catch (_: Exception) {
            ""
        }
    }

    val endTimeFormatted: String by lazy {
        try {
            val localDateTime = LocalDateTime.parse(endTimeRaw)
            "${localDateTime.hour.toString().padStart(2, '0')}:${
                localDateTime.minute.toString().padStart(2, '0')
            }"
        } catch (_: Exception) {
            ""
        }
    }

    fun getStatus(
        now: Instant,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): SessionStatus {
        return try {
            val startInstant = LocalDateTime.parse(startTimeRaw).toInstant(timeZone)
            val endInstant = LocalDateTime.parse(endTimeRaw).toInstant(timeZone)
            when {
                now < startInstant -> SessionStatus.UPCOMING
                now in startInstant..endInstant -> SessionStatus.ONGOING
                else -> SessionStatus.COMPLETED
            }
        } catch (_: Exception) {
            SessionStatus.UPCOMING
        }
    }
}
