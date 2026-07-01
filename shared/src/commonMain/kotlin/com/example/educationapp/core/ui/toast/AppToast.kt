package com.example.educationapp.core.ui.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_launcher
import org.jetbrains.compose.resources.DrawableResource

/**
 * Toast dùng chung cho toàn bộ ứng dụng.
 * Tự động hiển thị logo của ứng dụng ở đầu dòng, xử lý xuống dòng khi văn bản dài
 * và tự co giãn kích thước phù hợp cho cả màn hình điện thoại lẫn máy tính bảng (tablet).
 *
 * @param message Nội dung thông báo hiển thị.
 * @param visible Trạng thái hiển thị (True: hiện, False: ẩn).
 * @param modifier Modifier tùy chỉnh.
 * @param logoIcon Icon logo hiển thị ở đầu dòng (mặc định lấy logo ứng dụng).
 * @param maxLines Số dòng tối đa hiển thị (mặc định là 3 dòng, sau đó sẽ hiển thị dạng ellipsis ...).
 */
@Composable
fun AppToast(
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    logoIcon: DrawableResource = Res.drawable.ic_launcher,
    maxLines: Int = 3
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
            .zIndex(99f) // Đảm bảo luôn nằm trên các view khác
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
            shape = RoundedCornerShape(AppDimen.p12),
            elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p6),
            modifier = Modifier
                .widthIn(max = AppDimen.p400)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = AppDimen.p16,
                    vertical = AppDimen.p12
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                AppIcon(
                    drawableRes = logoIcon,
                    iconModifier = Modifier.size(AppDimen.p24),
                    boxModifier = Modifier.size(AppDimen.p24).background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(
                            AppDimen.p45
                        )
                    ),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                AppText(
                    text = message,
                    fontSize = AppDimen.s14,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    textAlign = TextAlign.Start,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(
                        1f,
                        fill = false
                    ) // Cho phép co giãn tự động theo text và xuống dòng khi vượt quá giới hạn
                )
            }
        }
    }
}
