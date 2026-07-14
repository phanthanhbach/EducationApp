package com.example.educationapp.presentation.screen.parent.child_attendance.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.parent_attendance_rate_label
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun AttendanceProgressRing(
    rate: Double,
    modifier: Modifier = Modifier,
    size: Dp = AppDimen.p90,
    strokeWidth: Dp = AppDimen.p8
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { (rate / 100.0).coerceIn(0.0..1.0).toFloat() },
            modifier = Modifier.fillMaxSize(),
            color = when {
                rate >= 80 -> Color(0xFF4CAF50)
                rate >= 50 -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            },
            strokeWidth = strokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val roundedRate = (rate * 10).roundToInt() / 10.0
            AppText(
                text = "$roundedRate%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            AppText(
                text = stringResource(Res.string.parent_attendance_rate_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
