package com.example.educationapp.presentation.screen.main.tab.component

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
import kotlinx.datetime.LocalDate

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
                    text = "Today's classes",
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
                    text = "COMING UP",
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
                text = "TODAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            val formattedDate = remember(today) {
                val monthName = getMonthNameEnglish(today.monthNumber)
                "$monthName ${today.dayOfMonth}, ${today.year}"
            }
            AppText(
                text = formattedDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppText(
                text = "No classes at the center today",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF192252),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = "Your timetable has no sessions on this date. Open Schedule to see other days.",
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
                    text = "View schedule",
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

private fun getMonthNameEnglish(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }
}
