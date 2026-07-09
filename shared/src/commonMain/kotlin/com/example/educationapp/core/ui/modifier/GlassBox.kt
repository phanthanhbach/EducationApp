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
import dev.chrisbanes.haze.HazeState

/**
 * Một container hỗ trợ hiệu ứng kính (Glassmorphism) hoạt động nhất quán trên cả Android và iOS.
 *
 * @param modifier Modifier dùng để định kích thước, khoảng cách và căn lề cho container.
 * @param shape Hình dạng bo góc (Shape) của kính (mặc định: bo góc 16.dp).
 * @param blurRadius Bán kính làm mờ (Blur) của kính (mặc định: 15.dp).
 * @param containerColor Màu sắc phủ lên kính (mặc định: Trắng trong suốt alpha 0.1f).
 * @param contentAlignment Căn lề nội dung bên trong kính (mặc định: Căn giữa).
 * @param hazeState Trạng thái HazeState dùng để liên kết với hazeSource trên nền màn hình, tạo hiệu ứng mờ thực sự trên Android.
 * @param borderColor Màu viền tùy chỉnh của tấm kính (mặc định: null, dùng màu outlineVariant).
 * @param shadowColor Màu bóng đổ tùy chỉnh của tấm kính (mặc định: null, dùng màu tự động).
 * @param content Nội dung hiển thị bên trên lớp kính.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 15.dp,
    containerColor: Color = Color.White.copy(alpha = 0.1f),
    contentAlignment: Alignment = Alignment.Center,
    hazeState: HazeState? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    content: @Composable () -> Unit
) {
    PlatformGlassContainer(
        modifier = modifier,
        shape = shape,
        blurRadius = blurRadius,
        color = containerColor,
        borderAlpha = 0.3f,
        hazeState = hazeState,
        borderColor = borderColor,
        shadowColor = shadowColor
    ) {
        Box(
            modifier = Modifier.align(contentAlignment),
            contentAlignment = contentAlignment
        ) {
            content()
        }
    }
}
