package com.example.educationapp.core.util

import androidx.compose.runtime.Composable
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.calendar_month_1
import educationapp.shared.generated.resources.calendar_month_10
import educationapp.shared.generated.resources.calendar_month_11
import educationapp.shared.generated.resources.calendar_month_12
import educationapp.shared.generated.resources.calendar_month_2
import educationapp.shared.generated.resources.calendar_month_3
import educationapp.shared.generated.resources.calendar_month_4
import educationapp.shared.generated.resources.calendar_month_5
import educationapp.shared.generated.resources.calendar_month_6
import educationapp.shared.generated.resources.calendar_month_7
import educationapp.shared.generated.resources.calendar_month_8
import educationapp.shared.generated.resources.calendar_month_9
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
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
    @Composable
    fun getMonthDisplayName(date: LocalDate): String {
        val stringRes = when (date.month.number) {
            1 -> Res.string.calendar_month_1
            2 -> Res.string.calendar_month_2
            3 -> Res.string.calendar_month_3
            4 -> Res.string.calendar_month_4
            5 -> Res.string.calendar_month_5
            6 -> Res.string.calendar_month_6
            7 -> Res.string.calendar_month_7
            8 -> Res.string.calendar_month_8
            9 -> Res.string.calendar_month_9
            10 -> Res.string.calendar_month_10
            11 -> Res.string.calendar_month_11
            12 -> Res.string.calendar_month_12
            else -> Res.string.calendar_month_1
        }
        return stringResource(stringRes)
    }
}
