package com.example.educationapp.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

object DateTimeFormatter {

    /**
     * Formats an ISO-8601 LocalDateTime string (e.g. "2026-06-01T17:52:55.422942")
     * into a readable format "dd/MM/yyyy · HH:mm".
     * If parsing fails or the input is blank, returns the original input string.
     */
    fun formatToDateTimeString(isoString: String?): String? {
        if (isoString.isNullOrBlank()) return null
        return try {
            val localDateTime = LocalDateTime.parse(isoString)
            val day = localDateTime.date.day.toString().padStart(2, '0')
            val month = localDateTime.date.month.number.toString().padStart(2, '0')
            val year = localDateTime.date.year
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            "$day/$month/$year · $hour:$minute"
        } catch (_: Exception) {
            isoString
        }
    }

    /**
     * Formats an ISO-8601 LocalDateTime string (e.g. "2026-07-01T13:39:21.797345")
     * into a "HH:mm" formatted string.
     * If parsing fails, returns null.
     */
    fun formatToTimeString(isoString: String?): String? {
        if (isoString.isNullOrBlank()) return null
        return try {
            val localDateTime = LocalDateTime.parse(isoString)
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            "$hour:$minute"
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Parses an ISO-8601 LocalDateTime string into a Pair of (timePart, datePart)
     * e.g. "2026-06-01T17:52:55.422942" -> Pair("17:52:55", "2026-06-01")
     */
    fun parseDateTimeParts(dateTimeStr: String?): Pair<String, String>? {
        if (dateTimeStr.isNullOrBlank()) return null
        return try {
            val tIndex = dateTimeStr.indexOf('T')
            if (tIndex != -1) {
                val datePart = dateTimeStr.substring(0, tIndex)
                val timePart = dateTimeStr.substring(tIndex + 1, minOf(tIndex + 9, dateTimeStr.length))
                Pair(timePart, datePart)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
