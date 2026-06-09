package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_door_open_24dp
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardScheduleCard(
    schedule: ScheduleSessionUiModel,
    isTeacher: Boolean,
    isComingUp: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardModifier = if (isTeacher) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Badge
            val badgeBgColor = if (isComingUp) {
                Color(0xFFECEEFC)
            } else {
                AppColor.Primary
            }
            val badgeTextColor = if (isComingUp) {
                AppColor.Primary
            } else {
                Color.White
            }

            val (topText, bottomText) = if (isComingUp) {
                val day = schedule.date.dayOfMonth.toString().padStart(2, '0')
                val month = schedule.date.monthNumber.toString().padStart(2, '0')
                day to month
            } else {
                val parts = schedule.startTimeFormatted.split(":")
                val hour = parts.getOrNull(0) ?: "00"
                val min = parts.getOrNull(1) ?: "00"
                hour to min
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppText(
                        text = topText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        lineHeight = 16.sp
                    )
                    AppText(
                        text = bottomText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AppText(
                    text = schedule.className,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Session Number on its own row
                Spacer(modifier = Modifier.height(2.dp))
                AppText(
                    text = "Session #${schedule.sessionNumber}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Show subtitle if subjectName != className and not blank
                if (schedule.subjectName != schedule.className && schedule.subjectName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    AppText(
                        text = schedule.subjectName,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Info (styled the same for both today and coming up)
                AppText(
                    text = "${schedule.startTimeFormatted} – ${schedule.endTimeFormatted} · ${schedule.room}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Right Button for Teachers (Today's classes only)
            if (isTeacher && !isComingUp) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColor.Primary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_door_open_24dp),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AppText(
                        text = "Open",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
