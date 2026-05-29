package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import platform.UIKit.UIBlurEffect
import platform.UIKit.UIBlurEffectStyle
import platform.UIKit.UIVisualEffectView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import com.example.educationapp.core.util.liquidGlass
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformGlassContainer(
    modifier: Modifier,
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        UIKitView(
            factory = {
                val blurEffect = UIBlurEffect.effectWithStyle(UIBlurEffectStyle.UIBlurEffectStyleRegular)
                val visualEffectView = UIVisualEffectView(effect = blurEffect)
                visualEffectView
            },
            modifier = Modifier.matchParentSize().clip(shape),
            update = { _ -> },
            properties = UIKitInteropProperties()
        )

        // Thêm lớp overlay màu và viền (như đã định nghĩa ở liquidGlass)
        Box(
            modifier = Modifier
                .matchParentSize()
                .liquidGlass(
                    shape = shape,
                    blurRadius = 0.dp, // Đã có native blur nên set 0
                    color = color.copy(alpha = 0.1f), // Làm nhẹ màu lại
                    borderAlpha = borderAlpha
                )
        )
        
        content()
    }
}
