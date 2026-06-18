package com.example.educationapp.domain.enums

enum class TeacherStatus(val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    ON_LEAVE("ON_LEAVE");

    companion object {
        fun fromString(value: String?): TeacherStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.value.equals(value, ignoreCase = true) }
                ?: ACTIVE
        }
    }
}
