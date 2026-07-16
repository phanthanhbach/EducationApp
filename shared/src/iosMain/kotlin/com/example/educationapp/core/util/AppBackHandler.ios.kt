package com.example.educationapp.core.util

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS does not have a hardware back button/system back swipe in the same manner.
    // Exiting the app is managed by the OS via the Home swipe/button.
}
