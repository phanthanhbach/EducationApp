package com.example.educationapp.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual fun Modifier.liquidGlass(
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float
): Modifier = this
    .clip(shape)
    .background(color)
    .border(
        width = 0.5.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        shape = shape
    )
    .blur(blurRadius)
