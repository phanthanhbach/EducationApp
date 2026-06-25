package com.example.educationapp.core.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import dev.chrisbanes.haze.HazeState

/**
 * Định nghĩa expect cho hiệu ứng kính để có thể tối ưu riêng cho từng nền tảng.
 */
expect fun Modifier.liquidGlass(
    shape: Shape,
    hazeState: HazeState? = null,
    blurRadius: Dp = 20.dp,
    color: Color = Color.White.copy(alpha = 0.15f),
    borderAlpha: Float = 0.3f
): Modifier

inline fun Modifier.conditional(
    condition: Boolean,
    crossinline block: Modifier.() -> Modifier
): Modifier = if (condition) then(block(Modifier)) else this

inline fun <T> Modifier.optional(
    value: T?,
    crossinline block: Modifier.(T) -> Modifier
): Modifier = value?.let { block(it) } ?: this

fun Modifier.enable(): Modifier = then(Modifier.alpha(1f))

fun Modifier.disable(): Modifier = then(Modifier.alpha(0.5f))

fun Modifier.enableOrDisable(isReadOnly: Boolean): Modifier {
    return when (isReadOnly) {
        true -> disable()
        else -> enable()
    }
}

fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.then(
        Modifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            role = role,
            onClick = onClick
        )
    )
}

fun Modifier.shimmerEffect(
    durationMillis: Int = 1200
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    )

    this.then(
        Modifier.drawBehind {
            val width = size.width
            val height = size.height
            if (width > 0 && height > 0) {
                val xOffset = translateAnimation.value * width
                drawRect(
                    brush = Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(x = xOffset, y = 0f),
                        end = Offset(x = xOffset + width, y = height)
                    )
                )
            }
        }
    )
}