package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.TeacherCheckInResult
import educationapp.shared.generated.resources.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecentCheckInsSection(
    totalCheckIns: Int,
    checkIns: List<TeacherCheckInResult>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(AppDimen.p16),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
    ) {
        // Section Header
        SectionHeader(
            title = stringResource(Res.string.dashboard_teaching_checkin_history)
        )

        // Total check-in label
        AppText(
            text = stringResource(Res.string.dashboard_total_checkins, totalCheckIns),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
        )

        // List of check-ins (limit to 3 or items present)
        if (checkIns.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppText(
                        text = stringResource(Res.string.dashboard_teaching_checkin_empty),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                checkIns.take(3).forEach { checkin ->
                    CheckInItemCard(
                        checkin = checkin,
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckInItemCard(
    checkin: TeacherCheckInResult,
    modifier: Modifier = Modifier
) {
    val isLate = checkin.status == "LATE"

    val checkinTimeFormatted = remember(checkin.checkinTime) {
        if (checkin.checkinTime.isNullOrBlank()) null else {
            try {
                val localDateTime = LocalDateTime.parse(checkin.checkinTime)
                val day = localDateTime.date.day.toString().padStart(2, '0')
                val month = localDateTime.date.month.number.toString().padStart(2, '0')
                val year = localDateTime.date.year
                val hour = localDateTime.hour.toString().padStart(2, '0')
                val minute = localDateTime.minute.toString().padStart(2, '0')
                "$day/$month/$year · $hour:$minute"
            } catch (e: Exception) {
                checkin.checkinTime
            }
        }
    }

    val checkoutTimeText = remember(checkin.checkoutTime, checkin.checkedOut) {
        if (checkin.checkedOut == true) {
            if (!checkin.checkoutTime.isNullOrBlank()) {
                try {
                    val localDateTime = LocalDateTime.parse(checkin.checkoutTime)
                    val hour = localDateTime.hour.toString().padStart(2, '0')
                    val minute = localDateTime.minute.toString().padStart(2, '0')
                    hour to minute
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    val badgeBgColor = if (isLate) AppColor.Error.copy(alpha = 0.1f) else AppColor.Success.copy(alpha = 0.1f)
    val badgeTextColor = if (isLate) AppColor.Error else AppColor.Success

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Class Title & Session Number
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AppText(
                    text = checkin.className ?: "Lớp học",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AppText(
                    text = stringResource(Res.string.dashboard_session_number_format, checkin.sessionNumber),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Check-in Info & Badge on the same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    text = stringResource(Res.string.dashboard_checkin_time_format, checkinTimeFormatted ?: "N/A"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    AppText(
                        text = if (isLate) {
                            stringResource(Res.string.dashboard_checkin_status_late, checkin.lateMinutes ?: 0)
                        } else {
                            stringResource(Res.string.dashboard_checkin_status_on_time)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }

            // Check-out Info below
            AppText(
                text = if (checkin.checkedOut == true) {
                    if (checkoutTimeText != null) {
                        stringResource(Res.string.dashboard_checkout_time_format, "${checkoutTimeText.first}:${checkoutTimeText.second}")
                    } else {
                        stringResource(Res.string.dashboard_checkout_done)
                    }
                } else {
                    stringResource(Res.string.dashboard_checkout_pending)
                },
                fontSize = 12.sp,
                color = if (checkin.checkedOut == true) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                } else {
                    AppColor.Warning
                }
            )
        }
    }
}
