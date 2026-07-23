package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import kotlinx.cinterop.ExperimentalForeignApi

import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformGlassContainer(
    modifier: Modifier,
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float,
    hazeState: HazeState?,
    borderColor: Color?,
    shadowColor: Color?,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseBorderColor = borderColor ?: MaterialTheme.colorScheme.outlineVariant
    val effectiveShadowColor = shadowColor ?: (borderColor ?: Color.Black)

    // Specular highlight border brush: top-left bright shine + bottom-right contrast stroke
    val borderBrush = if (borderColor != null) {
        Brush.linearGradient(
            colors = listOf(
                borderColor.copy(alpha = borderAlpha * 1.5f),
                borderColor.copy(alpha = borderAlpha * 0.5f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = if (isDark) {
                listOf(
                    Color.White.copy(alpha = 0.45f * borderAlpha),
                    baseBorderColor.copy(alpha = 0.15f * borderAlpha)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.95f * borderAlpha),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f * borderAlpha)
                )
            }
        )
    }

    // Resolve background tint: if color is Unspecified, use adaptive translucent floating surface glass
    val glassTint = if (color != Color.Unspecified) {
        color
    } else {
        if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        }
    }

    val shadowElevation = if (isDark) 4.dp else 6.dp

    Box(
        modifier = modifier.shadow(
            elevation = shadowElevation,
            shape = shape,
            clip = false,
            ambientColor = effectiveShadowColor.copy(alpha = if (isDark) 0.12f else 0.08f),
            spotColor = effectiveShadowColor.copy(alpha = if (isDark) 0.12f else 0.08f)
        )
    ) {
        if (hazeState != null && blurRadius > 0.dp) {
            // 1. Haze blur layer with floating specular border
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .hazeEffect(state = hazeState) {
                        blurEffect {
                            this.blurRadius = blurRadius
                            this.noiseFactor = 0f
                            this.colorEffects = listOf(
                                HazeColorEffect.tint(glassTint)
                            )
                        }
                    }
                    .border(1.dp, borderBrush, shape)
            )
        } else {
            // 2. Theme-aware floating frosted glass container with specular edge highlight
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(color = glassTint, shape = shape)
                    .border(1.dp, borderBrush, shape)
            )
        }
        
        content()
    }
}
