package com.example.educationapp.core.ui.modifier

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState

/**
 * Container nền kính (Glassmorphic) sử dụng các cơ chế vẽ tối ưu riêng biệt cho từng nền tảng.
 *
 * @param modifier Modifier dùng để định kích thước, khoảng cách và căn lề cho container.
 * @param shape Hình dạng bo góc (Shape) của tấm kính (ví dụ: RoundedCornerShape).
 * @param blurRadius Bán kính làm mờ (Blur) của kính (chỉ áp dụng ở nhánh fallback hoặc iOS).
 * @param color Màu sắc chủ đạo phủ lên tấm kính (thường là màu trắng/tối có alpha nhẹ).
 * @param borderAlpha Độ mờ (opacity) của viền phản quang xung quanh tấm kính.
 * @param hazeState Trạng thái HazeState dùng để liên kết với hazeSource trên nền màn hình, tạo hiệu ứng mờ thực sự trên Android.
 * @param content Nội dung hiển thị nằm trên bề mặt tấm kính.
 */
@Composable
expect fun PlatformGlassContainer(
    modifier: Modifier,
    shape: Shape,
    blurRadius: Dp,
    color: Color,
    borderAlpha: Float,
    hazeState: HazeState? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
)

