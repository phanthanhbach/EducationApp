package com.example.educationapp.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

object CalendarHelper {

    /**
     * Get the current local date.
     */
    fun getCurrentDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    /**
     * Get the list of 7 days (Monday to Sunday) for the week containing [date].
     */
    fun getWeekDates(date: LocalDate): List<LocalDate> {
        val daysToSubtract = date.dayOfWeek.ordinal // Monday is 0, Sunday is 6
        val mondayEpochDays = date.toEpochDays() - daysToSubtract
        return (0..6).map { LocalDate.fromEpochDays(mondayEpochDays + it) }
    }

    /**
     * Get a list of 42 days (6 weeks) to display a complete monthly grid for [date].
     * Padded with days from the previous and next month to start on Monday.
     */
    fun getMonthGridDates(date: LocalDate): List<LocalDate> {
        val firstDayOfMonth = LocalDate(date.year, date.month.number, 1)
        val firstDayOfWeekOrdinal = firstDayOfMonth.dayOfWeek.ordinal // Monday = 0
        val gridStartEpochDays = firstDayOfMonth.toEpochDays() - firstDayOfWeekOrdinal
        return (0 until 42).map { LocalDate.fromEpochDays(gridStartEpochDays + it) }
    }

    /**
     * Get the display name of the month (e.g., "June" or "Tháng 6").
     */
    fun getMonthDisplayName(date: LocalDate): String {
        return when (date.month.number) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }
}
