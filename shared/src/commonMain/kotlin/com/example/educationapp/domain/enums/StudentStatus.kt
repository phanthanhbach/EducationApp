package com.example.educationapp.domain.enums

import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.student_status_active
import educationapp.shared.generated.resources.student_status_inactive
import educationapp.shared.generated.resources.student_status_suspended
import educationapp.shared.generated.resources.student_status_graduated
import org.jetbrains.compose.resources.StringResource

enum class StudentStatus(val value: String, val labelRes: StringResource) {
    ACTIVE("ACTIVE", Res.string.student_status_active),
    INACTIVE("INACTIVE", Res.string.student_status_inactive),
    SUSPENDED("SUSPENDED", Res.string.student_status_suspended),
    GRADUATED("GRADUATED", Res.string.student_status_graduated);

    companion object {
        fun fromString(value: String?): StudentStatus {
            val normalized = value?.uppercase()
            return entries.find { it.name == normalized || it.value == normalized }
                ?: when (value?.lowercase()) {
                    "completed" -> GRADUATED
                    "dropped", "cancelled" -> INACTIVE
                    else -> ACTIVE
                }
        }
    }
}
