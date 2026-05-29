package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Container nền kính sử dụng hiệu ứng native của từng nền tảng.
 */
@Composable
expect fun PlatformGlassContainer(
    modifier: Modifier,
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float,
    content: @Composable BoxScope.() -> Unit
)
