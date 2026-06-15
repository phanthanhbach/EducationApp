package com.example.educationapp.domain.enums

enum class AppLanguage(val localeTag: String) {
    ENGLISH("en"),
    VIETNAMESE("vi");

    companion object {
        fun fromLocaleTag(tag: String?): AppLanguage =
            entries.find { it.localeTag.equals(tag, ignoreCase = true) } ?: ENGLISH
    }
}
