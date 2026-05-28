package com.example.educationapp.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppColor.Primary,
    secondary = AppColor.Secondary,
    tertiary = AppColor.Tertiary,
    background = AppColor.BackgroundLight,
    surface = AppColor.SurfaceLight,
    error = AppColor.Error,
    onPrimary = AppColor.SurfaceLight,
    onSecondary = AppColor.SurfaceLight,
    onTertiary = AppColor.TextPrimaryLight,
    onBackground = AppColor.TextPrimaryLight,
    onSurface = AppColor.TextPrimaryLight,
    onSurfaceVariant = AppColor.TextSecondaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColor.Secondary, // Use lighter version for primary in dark mode
    secondary = AppColor.Primary,
    tertiary = AppColor.Tertiary,
    background = AppColor.BackgroundDark,
    surface = AppColor.SurfaceDark,
    error = AppColor.Error,
    onPrimary = AppColor.BackgroundDark,
    onSecondary = AppColor.BackgroundDark,
    onTertiary = AppColor.TextPrimaryDark,
    onBackground = AppColor.TextPrimaryDark,
    onSurface = AppColor.TextPrimaryDark,
    onSurfaceVariant = AppColor.TextSecondaryDark
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(),
        content = content
    )
}
