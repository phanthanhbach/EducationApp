package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.calendar_month_1
import educationapp.shared.generated.resources.calendar_month_10
import educationapp.shared.generated.resources.calendar_month_11
import educationapp.shared.generated.resources.calendar_month_12
import educationapp.shared.generated.resources.calendar_month_2
import educationapp.shared.generated.resources.calendar_month_3
import educationapp.shared.generated.resources.calendar_month_4
import educationapp.shared.generated.resources.calendar_month_5
import educationapp.shared.generated.resources.calendar_month_6
import educationapp.shared.generated.resources.calendar_month_7
import educationapp.shared.generated.resources.calendar_month_8
import educationapp.shared.generated.resources.calendar_month_9
import educationapp.shared.generated.resources.dashboard_coming_up_title
import educationapp.shared.generated.resources.dashboard_date_format
import educationapp.shared.generated.resources.dashboard_no_classes_desc
import educationapp.shared.generated.resources.dashboard_no_classes_title
import educationapp.shared.generated.resources.dashboard_today_classes_title
import educationapp.shared.generated.resources.dashboard_today_label
import educationapp.shared.generated.resources.dashboard_view_schedule_btn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource


@Composable
fun UpcomingSchedulesSection(
    role: AppRole,
    schedules: List<ScheduleSessionUiModel>,
    today: LocalDate,
    onScheduleClick: (ScheduleSessionUiModel) -> Unit,
    onViewScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTeacher = role == AppRole.TEACHER

    // Group schedules into Today and Coming Up
    val todaySchedules = remember(schedules, today) {
        schedules.filter { it.date == today }
    }
    val upcomingSchedules = remember(schedules, today) {
        schedules.filter { it.date > today }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
    ) {
        if (todaySchedules.isEmpty()) {
            // No classes today card (Image 1)
            NoClassesTodayCard(
                today = today,
                onViewScheduleClick = onViewScheduleClick
            )
        } else {
            // Today's classes section (Image 2)
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p8)) {
                AppText(
                    text = stringResource(Res.string.dashboard_today_classes_title),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    letterSpacing = 1.sp,
                    allCaps = true
                )

                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p10)) {
                    todaySchedules.forEach { schedule ->
                        DashboardScheduleCard(
                            schedule = schedule,
                            isTeacher = isTeacher,
                            isComingUp = false,
                            onClick = { onScheduleClick(schedule) }
                        )
                    }
                }
            }
        }

        // COMING UP section (Image 2)
        if (upcomingSchedules.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p8)) {
                AppText(
                    text = stringResource(Res.string.dashboard_coming_up_title),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    letterSpacing = 1.sp,
                    allCaps = true
                )

                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p10)) {
                    upcomingSchedules.forEach { schedule ->
                        DashboardScheduleCard(
                            schedule = schedule,
                            isTeacher = isTeacher,
                            isComingUp = true,
                            onClick = { onScheduleClick(schedule) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoClassesTodayCard(
    today: LocalDate,
    onViewScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                text = stringResource(Res.string.dashboard_today_label),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            val monthRes = remember(today.month.number) {
                when (today.month.number) {
                    1 -> Res.string.calendar_month_1
                    2 -> Res.string.calendar_month_2
                    3 -> Res.string.calendar_month_3
                    4 -> Res.string.calendar_month_4
                    5 -> Res.string.calendar_month_5
                    6 -> Res.string.calendar_month_6
                    7 -> Res.string.calendar_month_7
                    8 -> Res.string.calendar_month_8
                    9 -> Res.string.calendar_month_9
                    10 -> Res.string.calendar_month_10
                    11 -> Res.string.calendar_month_11
                    12 -> Res.string.calendar_month_12
                    else -> Res.string.calendar_month_1
                }
            }
            val monthName = stringResource(monthRes)
            val formattedDate = stringResource(
                Res.string.dashboard_date_format, monthName,
                today.day, today.year
            )
            AppText(
                text = formattedDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppText(
                text = stringResource(Res.string.dashboard_no_classes_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF192252),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = stringResource(Res.string.dashboard_no_classes_desc),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewScheduleClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AppText(
                    text = stringResource(Res.string.dashboard_view_schedule_btn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColor.Primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                AppText(
                    text = "→",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColor.Primary
                )
            }
        }
    }
}

