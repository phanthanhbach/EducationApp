package com.example.educationapp.core.util

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.ExperimentalComposeUiApi

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(text: String): ClipEntry {
    return ClipEntry.withPlainText(text)
}
