package com.example.educationapp.domain.enums

import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.assignment_not_submitted
import educationapp.shared.generated.resources.assignment_submitted
import org.jetbrains.compose.resources.StringResource

enum class AssignmentFilter {
    SUBMITTED,
    NOT_SUBMITTED;

    fun toBoolean(): Boolean = this == SUBMITTED

    fun getLabelRes(): StringResource {
        return when (this) {
            SUBMITTED -> Res.string.assignment_submitted
            NOT_SUBMITTED -> Res.string.assignment_not_submitted
        }
    }

    companion object {
        fun fromBoolean(value: Boolean): AssignmentFilter =
            if (value) SUBMITTED else NOT_SUBMITTED
    }
}
