package com.example.educationapp.core.util

import androidx.compose.ui.platform.ClipEntry

/**
 * Creates a [ClipEntry] containing the given plain text.
 * Used for copy-to-clipboard functionality with the modern [androidx.compose.ui.platform.LocalClipboard].
 */
expect fun clipEntryOf(text: String): ClipEntry
