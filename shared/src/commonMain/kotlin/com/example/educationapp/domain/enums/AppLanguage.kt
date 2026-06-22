package com.example.educationapp.domain.enums

enum class AppLanguage(
    val localeTag: String,
    val displayName: String,
    val flagEmoji: String
) {
    ENGLISH("en", "English", "🇺🇸"),
    VIETNAMESE("vi", "Tiếng Việt", "🇻🇳");

    companion object {
        fun fromLocaleTag(tag: String?): AppLanguage =
            entries.find { it.localeTag.equals(tag, ignoreCase = true) } ?: ENGLISH
    }
}
