package com.example.educationapp.presentation.screen.session.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.session_info_title
import educationapp.shared.generated.resources.session_room
import educationapp.shared.generated.resources.session_time
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionInfoSection(
    session: ScheduleSessionUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AppDimen.p12),
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p2),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p20),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppText(
                text = stringResource(Res.string.session_info_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                // Time block
                InfoBlock(
                    icon = Res.drawable.ic_calendar_month_filled_24dp,
                    iconTint = AppColor.Primary,
                    title = stringResource(Res.string.session_time),
                    value = "${session.startTimeFormatted} - ${session.endTimeFormatted}",
                    modifier = Modifier.weight(1f)
                )

                // Room block
                InfoBlock(
                    icon = Res.drawable.ic_assignment_filled_24dp,
                    iconTint = AppColor.Primary,
                    title = stringResource(Res.string.session_room),
                    value = session.room,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoBlock(
    icon: DrawableResource,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimen.p12))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(AppDimen.p12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            drawableRes = icon,
            iconModifier = Modifier.size(AppDimen.p20),
            boxModifier = Modifier
                .size(AppDimen.p40)
                .clip(RoundedCornerShape(AppDimen.p8))
                .background(iconTint.copy(alpha = 0.15f)),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(AppDimen.p12))
        Column {
            AppText(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AppText(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
