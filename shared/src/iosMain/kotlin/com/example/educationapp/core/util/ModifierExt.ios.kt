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

import dev.chrisbanes.haze.HazeState

actual fun Modifier.liquidGlass(
    shape: Shape,
    hazeState: HazeState?,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float
): Modifier = this
    .clip(shape)
    .background(color.copy(alpha = color.alpha * 0.8f)) 
    .border(
        width = 0.5.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = 0.1f)
            )
        ),
        shape = shape
    )
    .let {
        if (blurRadius > 0.dp) {
            it.blur(blurRadius)
        } else {
            it
        }
    }
