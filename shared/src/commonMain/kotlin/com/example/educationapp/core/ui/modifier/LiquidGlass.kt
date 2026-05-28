package com.example.educationapp.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
