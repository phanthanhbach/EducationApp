package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Alignment
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
import dev.chrisbanes.haze.HazeState

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
    Box(modifier = modifier) {
        if (blurRadius > 0.dp) {
            UIKitView(
                factory = {
                    UIVisualEffectView()
                },
                modifier = Modifier.matchParentSize().clip(shape),
                update = { visualEffectView ->
                    val isLight = color.luminance() > 0.5f
                    val style = if (isLight) {
                        UIBlurEffectStyle.UIBlurEffectStyleSystemUltraThinMaterialLight
                    } else {
                        UIBlurEffectStyle.UIBlurEffectStyleSystemUltraThinMaterialDark
                    }
                    visualEffectView.effect = UIBlurEffect.effectWithStyle(style)
                },
                properties = UIKitInteropProperties()
            )
        }

        // Draw a clean flat container background and border matching the theme
        val baseBorderColor = borderColor ?: MaterialTheme.colorScheme.outlineVariant

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(color = color, shape = shape)
                .border(
                    width = 1.dp,
                    color = baseBorderColor.copy(alpha = borderAlpha),
                    shape = shape
                )
        )
        
        content()
    }
}
