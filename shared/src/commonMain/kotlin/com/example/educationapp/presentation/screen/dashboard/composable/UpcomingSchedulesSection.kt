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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.model.ScheduleSessionUiModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_coming_up_title
import educationapp.shared.generated.resources.dashboard_no_classes_desc
import educationapp.shared.generated.resources.dashboard_no_classes_title
import educationapp.shared.generated.resources.dashboard_today_classes_title
import educationapp.shared.generated.resources.dashboard_today_label
import educationapp.shared.generated.resources.dashboard_view_schedule_btn
import kotlinx.datetime.LocalDate
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
        shape = RoundedCornerShape(AppDimen.p16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            AppDimen.p1,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimen.p1)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimen.p24, horizontal = AppDimen.p20),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                text = stringResource(Res.string.dashboard_today_label),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(AppDimen.p4))

            val formattedDate = CalendarHelper.getFormattedDate(today)
            AppText(
                text = formattedDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppDimen.p16))

            AppText(
                text = stringResource(Res.string.dashboard_no_classes_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppDimen.p8))

            AppText(
                text = stringResource(Res.string.dashboard_no_classes_desc),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = AppDimen.p16)
            )

            Spacer(modifier = Modifier.height(AppDimen.p20))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppDimen.p8))
                    .clickable { onViewScheduleClick() }
                    .padding(horizontal = AppDimen.p12, vertical = AppDimen.p6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AppText(
                    text = stringResource(Res.string.dashboard_view_schedule_btn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColor.Primary
                )
                Spacer(modifier = Modifier.width(AppDimen.p4))
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

