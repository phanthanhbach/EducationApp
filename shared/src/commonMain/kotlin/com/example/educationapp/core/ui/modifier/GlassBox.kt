package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Một container hỗ trợ hiệu ứng kính (Glassmorphism) sử dụng Native Blur trên iOS.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 15.dp,
    containerColor: Color = Color.White.copy(alpha = 0.1f),
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    PlatformGlassContainer(
        modifier = modifier,
        shape = shape,
        blurRadius = blurRadius,
        color = containerColor,
        borderAlpha = 0.3f
    ) {
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = contentAlignment
        ) {
            content()
        }
    }
}
