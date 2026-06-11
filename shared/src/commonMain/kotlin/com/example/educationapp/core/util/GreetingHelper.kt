package com.example.educationapp.core.util

import educationapp.shared.generated.resources.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock

object GreetingHelper {

    /**
     * Get the string resource for greeting based on the current hour.
     * With parameter [hasName] to select whether it has a placeholder for the name.
     */
    fun getGreetingStringRes(hasName: Boolean, hour: Int = getCurrentHour()): StringResource {
        return when (hour) {
            in 5..11 -> if (hasName) Res.string.greeting_morning else Res.string.greeting_morning_generic
            in 12..17 -> if (hasName) Res.string.greeting_afternoon else Res.string.greeting_afternoon_generic
            else -> if (hasName) Res.string.greeting_evening else Res.string.greeting_evening_generic
        }
    }

    private fun getCurrentHour(): Int {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    }
}
