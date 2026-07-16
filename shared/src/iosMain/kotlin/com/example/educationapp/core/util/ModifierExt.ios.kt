package com.example.educationapp.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

actual fun Modifier.liquidGlass(
    shape: Shape,
    hazeState: HazeState?,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float
): Modifier {
    val isLight = color.luminance() > 0.5f
    val startAlphaFactor = if (isLight) 1.02f else 1.3f
    val endAlphaFactor = if (isLight) 0.95f else 0.7f
    val borderColor = if (isLight) Color.Black else Color.White
    val borderAlphaMultiplier = if (isLight) 0.25f else 1.0f

    return this
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = (color.alpha * startAlphaFactor).coerceAtMost(1f)),
                    color.copy(alpha = color.alpha * endAlphaFactor)
                )
            ),
            shape = shape
        )
        .let { modifier ->
            if (hazeState != null) {
                modifier.hazeEffect(state = hazeState) {
                    blurEffect {
                        this.blurRadius = blurRadius
                        this.noiseFactor = 0f
                        this.colorEffects = listOf(
                            HazeColorEffect.tint(color.copy(alpha = color.alpha * 0.55f))
                        )
                    }
                }
            } else {
                modifier
            }
        }
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor.copy(alpha = borderAlpha * borderAlphaMultiplier),
                    borderColor.copy(alpha = borderAlpha * borderAlphaMultiplier * 0.5f),
                    borderColor.copy(alpha = borderAlpha * borderAlphaMultiplier * 0.2f)
                )
            ),
            shape = shape
        )
}
