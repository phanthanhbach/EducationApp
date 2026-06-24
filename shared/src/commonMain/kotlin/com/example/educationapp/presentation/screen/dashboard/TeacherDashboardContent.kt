package com.example.educationapp.presentation.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.shimmer.skeleton.DashboardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.dashboard.composable.RecentCheckInsSection
import com.example.educationapp.presentation.screen.dashboard.composable.SectionHeader
import com.example.educationapp.presentation.screen.dashboard.composable.TeacherRatingSummaryCard
import com.example.educationapp.presentation.screen.dashboard.composable.UpcomingSchedulesSection
import com.example.educationapp.presentation.screen.schedule.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.dashboard_btn_retry
import educationapp.shared.generated.resources.dashboard_upcoming_teaching_schedules
import org.jetbrains.compose.resources.stringResource

@Composable
fun TeacherDashboardContent(
    screenModel: TeacherDashboardScreenModel,
    onViewScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parentNavigator = LocalNavigator.currentOrThrow.parent
    val state by screenModel.state.collectAsState()

    when (val currentState = state) {
        is TeacherDashboardState.Loading -> {
            DashboardSkeleton(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16)
            )
        }

        is TeacherDashboardState.Error -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.1f
                    )
                ),
                border = BorderStroke(1.dp, AppColor.Error.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(AppDimen.p16),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppText(
                        text = currentState.message,
                        fontSize = 14.sp,
                        color = AppColor.Error,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { screenModel.loadDashboardData() },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                    ) {
                        AppText(text = stringResource(Res.string.dashboard_btn_retry), color = Color.White)
                    }
                }
            }
        }

        is TeacherDashboardState.Success -> {
            val today = remember { CalendarHelper.getCurrentDate() }

            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(AppDimen.p4)
            ) {
                TeacherRatingSummaryCard(ratingSummary = currentState.ratingSummary)

                // Upcoming Classes Section
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(AppDimen.p16),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    SectionHeader(
                        title = stringResource(Res.string.dashboard_upcoming_teaching_schedules)
                    )

                    UpcomingSchedulesSection(
                        role = AppRole.TEACHER,
                        schedules = currentState.upcomingSchedules,
                        today = today,
                        onScheduleClick = { schedule ->
                            parentNavigator?.push(SessionDetailScreen(schedule))
                        },
                        onViewScheduleClick = onViewScheduleClick
                    )
                }

                // Check-ins Section
                RecentCheckInsSection(
                    totalCheckIns = currentState.totalCheckIns,
                    checkIns = currentState.recentCheckIns
                )
            }
        }
    }
}
