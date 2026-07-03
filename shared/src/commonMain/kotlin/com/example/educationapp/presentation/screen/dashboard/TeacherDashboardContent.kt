package com.example.educationapp.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.shimmer.skeleton.DashboardSkeleton
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.dashboard.composable.RecentCheckInsSection
import com.example.educationapp.presentation.screen.dashboard.composable.SectionHeader
import com.example.educationapp.presentation.screen.dashboard.composable.TeacherRatingSummaryCard
import com.example.educationapp.presentation.screen.dashboard.composable.UpcomingSchedulesSection
import com.example.educationapp.presentation.screen.session.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardState
import educationapp.shared.generated.resources.Res
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
            ErrorStateView(
                modifier = Modifier.padding(AppDimen.p16),
                error = currentState.error,
                onRetry = { screenModel.loadDashboardData() }
            )
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
