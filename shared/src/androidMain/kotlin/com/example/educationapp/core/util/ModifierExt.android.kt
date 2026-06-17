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
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect

actual fun Modifier.liquidGlass(
    shape: Shape,
    hazeState: HazeState?,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float
): Modifier = this
    .clip(shape)
    .let {
        if (hazeState != null) {
            it.hazeEffect(state = hazeState) {
                blurEffect {
                    this.blurRadius = blurRadius
                    this.colorEffects = listOf(
                        HazeColorEffect.tint(color)
                    )
                }
            }
        } else {
            it.background(color)
        }
    }
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
    .let {
        if (hazeState == null && blurRadius > 0.dp) {
            it.blur(blurRadius)
        } else {
            it
        }
    }
