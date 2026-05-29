package com.example.educationapp.core.data

import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.ExperimentalSettingsImplementation
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class, ExperimentalSettingsImplementation::class)
fun createIosSecureSettings(): Settings {
    return KeychainSettings(service = "EducationAppSecureSettings")
}
