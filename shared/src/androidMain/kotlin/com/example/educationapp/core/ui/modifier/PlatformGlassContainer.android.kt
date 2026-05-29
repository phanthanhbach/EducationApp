package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

import com.example.educationapp.core.util.liquidGlass

@Composable
actual fun PlatformGlassContainer(
    modifier: Modifier,
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.liquidGlass(
            shape = shape,
            blurRadius = blurRadius,
            color = color,
            borderAlpha = borderAlpha
        )
    ) {
        content()
    }
}
