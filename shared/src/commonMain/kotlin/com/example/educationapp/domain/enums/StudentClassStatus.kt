package com.example.educationapp.domain.enums

import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.lb_status_dropped
import org.jetbrains.compose.resources.StringResource

enum class StudentClassStatus(val labelRes: StringResource) {
    ACTIVE(Res.string.lb_status_active),
    COMPLETED(Res.string.lb_status_completed),
    DROPPED(Res.string.lb_status_dropped);

    companion object {
        fun fromString(value: String?): StudentClassStatus? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
