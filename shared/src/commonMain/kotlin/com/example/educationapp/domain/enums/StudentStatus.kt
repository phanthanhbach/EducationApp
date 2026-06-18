package com.example.educationapp.domain.enums

enum class StudentStatus(val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    GRADUATED("GRADUATED");

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
