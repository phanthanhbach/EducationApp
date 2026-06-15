package com.example.educationapp.domain.enums

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): AppThemeMode =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}
