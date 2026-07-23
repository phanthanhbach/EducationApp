package com.example.educationapp.core.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

/**
 * Modifier tạo hiệu ứng kính (Glassmorphism) sử dụng Haze blur.
 *
 * Dùng cho các trường hợp không thể bọc trong [GlassBox] (ví dụ: AlertDialog, Sheet)
 * vì component bên ngoài có API cố định không cho phép wrap thêm container.
 *
 * Trên cả Android và iOS, modifier này sử dụng chung Haze library để tạo blur effect.
 * Nếu cần native iOS blur (UIVisualEffectView), hãy sử dụng [GlassBox] thay thế.
 *
 * @param shape Hình dạng bo góc.
 * @param hazeState Trạng thái HazeState để liên kết với hazeSource, tạo blur thực sự.
 * @param blurRadius Bán kính làm mờ.
 * @param color Màu sắc phủ lên kính.
 * @param borderAlpha Độ mờ (opacity) của viền phản quang.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    hazeState: HazeState? = null,
    blurRadius: Dp = 20.dp,
    color: Color = Color.Unspecified,
    borderAlpha: Float = 0.3f
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val resolvedColor = if (color != Color.Unspecified) {
        color
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    }

    val startAlphaFactor = if (!isDark) 1.02f else 1.3f
    val endAlphaFactor = if (!isDark) 0.95f else 0.7f
    val borderColor = if (!isDark) Color.Black else Color.White
    val borderAlphaMultiplier = if (!isDark) 0.25f else 1.0f

    this
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    resolvedColor.copy(alpha = (resolvedColor.alpha * startAlphaFactor).coerceAtMost(1f)),
                    resolvedColor.copy(alpha = resolvedColor.alpha * endAlphaFactor)
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
                            HazeColorEffect.tint(resolvedColor.copy(alpha = resolvedColor.alpha * 0.55f))
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