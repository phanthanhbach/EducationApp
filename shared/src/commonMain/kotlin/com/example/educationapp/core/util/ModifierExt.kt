package com.example.educationapp.core.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Định nghĩa expect cho hiệu ứng kính để có thể tối ưu riêng cho từng nền tảng.
 */
expect fun Modifier.liquidGlass(
    shape: Shape,
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