package com.example.educationapp.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
): Modifier = this
    // Luôn vẽ nền gradient kính làm mờ để tạo hiệu ứng chuyển sáng chéo (glossy highlights) đặc trưng của kính
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = color.alpha * 1.3f), // Tăng sáng góc trên bên trái để tương phản với nền
                color.copy(alpha = color.alpha * 0.7f)  // Giảm tối góc dưới bên phải
            )
        ),
        shape = shape
    )
    .let { modifier ->
        if (hazeState != null) {
            modifier.hazeEffect(state = hazeState) {
                blurEffect {
                    this.blurRadius = blurRadius
                    this.noiseFactor = 0f // Thiết lập tắt hoàn toàn nhiễu hạt bên trong blurEffect
                    this.colorEffects = listOf(
                        HazeColorEffect.tint(color.copy(alpha = color.alpha * 0.2f)) // Giảm tint để nổi bật gradient nền
                    )
                }
            }
        } else {
            modifier
        }
    }
    // 2. Viền phản quang gradient sắc nét giống như kính thật (không để bị biến mất ở góc dưới)
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),          // Góc trên-trái sáng nhất
                Color.White.copy(alpha = borderAlpha * 0.5f),    // Cạnh bên mờ hơn
                Color.White.copy(alpha = borderAlpha * 0.2f)     // Góc dưới-phải (vẫn hiển thị nhẹ)
            )
        ),
        shape = shape
    )

