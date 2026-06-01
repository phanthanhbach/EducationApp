package com.example.educationapp.domain.enums

enum class AppRole(val value: String) {
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    PARENT("PARENT"),
    UNKNOWN("UNKNOWN");

    val isMobileAccessAllowed: Boolean
        get() = this != UNKNOWN

    companion object {
        fun fromString(role: String): AppRole =
            entries.find { it.value.equals(role, ignoreCase = true) } ?: UNKNOWN
    }
}