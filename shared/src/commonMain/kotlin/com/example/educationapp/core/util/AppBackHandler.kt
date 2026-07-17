package com.example.educationapp.core.util

import androidx.compose.runtime.Composable

/**
 * Handle back press behavior in a platform-agnostic way.
 * On Android, this wraps [androidx.activity.compose.BackHandler].
 * On iOS, this is currently a no-op as iOS has its own gesture-based/home-swipe navigation managed by the OS.
 */
@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)
