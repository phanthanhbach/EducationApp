package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

import com.example.educationapp.core.ui.layout.LocalTopBarHazeState
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState

/**
 * Container hỗ trợ hiệu ứng kính (Glassmorphism) hoạt động nhất quán trên cả Android và iOS.
 *
 * ## Hành vi theo nền tảng:
 * - **iOS & Android**: Sử dụng [Haze] library để tạo blur mờ nội dung phía sau một cách tự động
 *   thông qua [LocalSharedHazeState] hoặc [LocalTopBarHazeState].
 *   Đồng bộ 100% với Theme của App và độc lập tuyệt đối với Cài đặt hệ thống thiết bị.
 *
 * @param modifier Modifier dùng để định kích thước, khoảng cách và căn lề cho container.
 * @param shape Hình dạng bo góc (Shape) của kính (mặc định: bo góc 16.dp).
 * @param blurRadius Bán kính làm mờ (Blur) của kính (mặc định: 15.dp).
 * @param containerColor Màu sắc phủ lên kính (mặc định: Unspecified - tự thích ứng theo theme surface).
 * @param contentAlignment Căn lề nội dung bên trong kính (mặc định: Căn giữa).
 * @param hazeState Trạng thái HazeState tùy chỉnh. Nếu không truyền, tự động đọc từ CompositionLocal.
 * @param borderColor Màu viền tùy chỉnh của tấm kính (mặc định: null, dùng màu outlineVariant).
 * @param shadowColor Màu bóng đổ tùy chỉnh của tấm kính (mặc định: null, dùng màu tự động).
 * @param content Nội dung hiển thị bên trên lớp kính.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 15.dp,
    containerColor: Color = Color.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    hazeState: HazeState? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    content: @Composable () -> Unit
) {
    val resolvedContainerColor = if (containerColor != Color.Unspecified) {
        containerColor
    } else {
        Color.Unspecified
    }

    PlatformGlassContainer(
        modifier = modifier,
        shape = shape,
        blurRadius = blurRadius,
        color = resolvedContainerColor,
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
