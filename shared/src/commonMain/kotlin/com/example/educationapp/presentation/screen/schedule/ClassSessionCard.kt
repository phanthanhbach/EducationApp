package com.example.educationapp.presentation.screen.schedule

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.SessionStatus
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.schedule_session_class
import educationapp.shared.generated.resources.schedule_session_time
import educationapp.shared.generated.resources.schedule_session_room
import educationapp.shared.generated.resources.schedule_session_attendance
import educationapp.shared.generated.resources.schedule_status_completed
import educationapp.shared.generated.resources.schedule_status_ongoing
import educationapp.shared.generated.resources.schedule_status_upcoming
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ClassSessionCard(
    session: ScheduleSessionUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { Clock.System.now() }
    val status = session.getStatus(now)
    val indicatorColor = when (status) {
        SessionStatus.COMPLETED -> AppColor.Success.copy(alpha = 0.8f)
        SessionStatus.ONGOING -> AppColor.Primary.copy(alpha = 0.8f)
        SessionStatus.UPCOMING -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = indicatorColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppDimen.p6)
                .clip(RoundedCornerShape(topStart = AppDimen.p12, bottomStart = AppDimen.p12))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = session.subjectName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(AppDimen.p8))

                    SessionStatusBadge(status = status)
                }

                Spacer(modifier = Modifier.height(AppDimen.p4))

                AppText(
                    text = stringResource(Res.string.schedule_session_class, session.className),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppDimen.p12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p16)
                ) {
                    Column {
                        AppText(
                            text = stringResource(Res.string.schedule_session_time),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        AppText(
                            text = "${session.startTimeFormatted} - ${session.endTimeFormatted}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        AppText(
                            text = stringResource(Res.string.schedule_session_room),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        AppText(
                            text = session.room,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (status == SessionStatus.COMPLETED && session.attendanceText.isNotBlank()) {
                        Column {
                            AppText(
                                text = stringResource(Res.string.schedule_session_attendance),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            AppText(
                                text = session.attendanceText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColor.Success
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionStatusBadge(
    status: SessionStatus,
    modifier: Modifier = Modifier
) {
    val badgeText = when (status) {
        SessionStatus.COMPLETED -> stringResource(Res.string.schedule_status_completed)
        SessionStatus.ONGOING -> stringResource(Res.string.schedule_status_ongoing)
        SessionStatus.UPCOMING -> stringResource(Res.string.schedule_status_upcoming)
    }

    val (badgeColor, textColor) = when (status) {
        SessionStatus.COMPLETED -> Pair(
            AppColor.Success.copy(alpha = 0.15f),
            AppColor.Success
        )

        SessionStatus.ONGOING -> Pair(
            AppColor.Primary.copy(alpha = 0.15f),
            AppColor.Primary
        )

        SessionStatus.UPCOMING -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor)
            .padding(horizontal = AppDimen.p8, vertical = AppDimen.p4),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (status == SessionStatus.ONGOING) {
                val infiniteTransition = rememberInfiniteTransition()
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(textColor.copy(alpha = pulseAlpha * 0.4f))
                        .zIndex(1f)
                )
            }

            AppText(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
