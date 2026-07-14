package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

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
    // Xác định bán kính bo góc của shape để vẽ bóng đổ mềm ăn khớp
    val density = LocalDensity.current
    val cornerRadius = if (shape is RoundedCornerShape) {
        with(density) {
            shape.topStart.toPx(Size.Zero, this).toDp()
        }
    } else {
        16.dp
    }

    // Sử dụng màu outlineVariant của MaterialTheme làm mặc định nếu không truyền borderColor
    val baseBorderColor = borderColor ?: MaterialTheme.colorScheme.outlineVariant
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            baseBorderColor.copy(alpha = borderAlpha * 1.5f),
            baseBorderColor.copy(alpha = borderAlpha * 0.5f)
        )
    )

    // Xác định xem theme hiện tại là tối hay sáng dựa vào độ sáng (luminance) của màu nền.
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val shadowAlpha = if (isDark) 0.10f else 0.04f

    // Màu bóng đổ: Mặc định dùng màu đen, nếu có màu viền trạng thái (như xanh lá/xanh dương)
    // thì dùng chính màu đó để tạo quầng sáng nhẹ (neon glow) cùng tông màu trạng thái dưới thẻ.
    val baseShadowColor = shadowColor ?: (borderColor ?: Color.Black)

    Box(
        modifier = modifier
            // 1. Đổ bóng mềm bằng cách loại trừ lòng kính (ClipOp.Difference) để giữ lòng kính trong suốt tuyệt đối
            .glassShadow(
                shapeRadius = cornerRadius,
                shadowBlur = 8.dp,
                offsetY = 2.dp,
                alpha = shadowAlpha,
                shadowColor = baseShadowColor
            )
            // 2. Cắt bo góc
            .clip(shape)
            // 3. Tô nền kính bán trong suốt
            .background(color = color, shape = shape)
            // 4. Viền kính thích ứng
            .border(
                width = 1.dp,
                brush = borderBrush,

                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Custom modifier đổ bóng mềm mại mô phỏng elevation nhưng không kích hoạt phần cứng Android shadow.
 * Sử dụng [ClipOp.Difference] để loại bỏ phần lòng của hình bo góc, chỉ vẽ bóng tỏa ra ngoài.
 * Giúp kính trong suốt 100% không bị đục.
 */
fun Modifier.glassShadow(
    shapeRadius: Dp,
    shadowBlur: Dp = 8.dp,
    offsetY: Dp = 2.dp,
    alpha: Float = 0.05f,
    shadowColor: Color = Color.Black
): Modifier = this.drawBehind {
    val radiusPx = shapeRadius.toPx()
    val blurPx = shadowBlur.toPx()
    val offsetYPx = offsetY.toPx()
    val shadowColorArgb = shadowColor.copy(alpha = alpha).toArgb()

    // 1. Tạo path đại diện cho hình dạng của card
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(Offset.Zero, size),
                cornerRadius = CornerRadius(radiusPx)
            )
        )
    }

    // 2. Loại trừ phần lòng card, chỉ cho phép vẽ bóng đổ lan ra phía ngoài viền
    clipPath(path, clipOp = ClipOp.Difference) {
        drawIntoCanvas { canvas ->
            @Suppress("DEPRECATION")
            val paint = Paint().asFrameworkPaint()
            paint.color = shadowColorArgb
            paint.style =
                android.graphics.Paint.Style.FILL // Sử dụng FILL để bóng đổ đầy đặn và thực tế

            // Cấu hình đổ bóng mờ của Android Paint
            paint.setShadowLayer(
                blurPx,
                0f,
                offsetYPx,
                shadowColorArgb
            )

            val rect = android.graphics.RectF(
                0f,
                0f,
                size.width,
                size.height
            )

            // Vẽ hình bo góc để tạo bóng đổ
            canvas.nativeCanvas.drawRoundRect(
                rect,
                radiusPx,
                radiusPx,
                paint
            )
        }
    }
}


