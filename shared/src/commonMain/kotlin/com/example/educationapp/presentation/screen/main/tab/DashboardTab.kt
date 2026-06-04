package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardState
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleSessionUiModel
import com.example.educationapp.presentation.screen.schedule.SessionDetailScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_dashboard_filled_24dp
import educationapp.shared.generated.resources.tab_dashboard
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.ic_person_filled_24dp
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class DashboardTab(private val role: AppRole) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_dashboard)
            val icon = painterResource(Res.drawable.ic_dashboard_filled_24dp)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(AppDimen.p16),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                // Header
                AppText(
                    text = "Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Role-based portals
                when (role) {
                    AppRole.STUDENT -> StudentDashboard()
                    AppRole.TEACHER -> TeacherDashboard()
                    AppRole.PARENT -> ParentDashboard()
                    AppRole.UNKNOWN -> {}
                }
            }
        }
    }

    @Composable
    private fun StudentDashboard() {
        Card(
            shape = RoundedCornerShape(AppDimen.p12),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimen.p16)) {
                AppText(
                    text = "Student Portal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppText(
                    text = "Welcome back! View your current grades, upcoming assignments and classes here.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }

    @Composable
    private fun TeacherDashboard() {
        val parentNavigator = LocalNavigator.currentOrThrow.parent
        val screenModel = koinScreenModel<TeacherDashboardScreenModel>()
        val state by screenModel.state.collectAsState()

        when (val currentState = state) {
            is TeacherDashboardState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColor.Primary)
                }
            }

            is TeacherDashboardState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
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
                            AppText(text = "Thử lại", color = Color.White)
                        }
                    }
                }
            }

            is TeacherDashboardState.Success -> {
                val today = remember { CalendarHelper.getCurrentDate() }
                
                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p20)) {
                    // Rating Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF475AD7), Color(0xFF8E97FD))
                                    )
                                )
                                .padding(AppDimen.p20),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Section: Rating
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    AppText(
                                        text = "${currentState.ratingSummary.averageRating}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    AppText(
                                        text = "★",
                                        fontSize = 28.sp,
                                        color = AppColor.Tertiary
                                    )
                                }
                                AppText(
                                    text = "Điểm đánh giá trung bình",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            // Vertical divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(50.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )

                            // Right Section: Details
                            Column(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .padding(start = AppDimen.p16),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_person_filled_24dp),
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        AppText(
                                            text = "${currentState.ratingSummary.totalRatings}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        AppText(
                                            text = "Lượt đánh giá",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_assignment_filled_24dp),
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        AppText(
                                            text = "${currentState.ratingSummary.totalFeedback}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        AppText(
                                            text = "Phản hồi/Nhận xét",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Upcoming Classes Section
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p12)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_calendar_month_filled_24dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            AppText(
                                text = "Lịch dạy 3 ngày tới",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (currentState.upcomingSchedules.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppDimen.p24),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppText(
                                        text = "Không có lịch dạy trong 3 ngày tới.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p10)) {
                                currentState.upcomingSchedules.forEach { schedule ->
                                    DashboardScheduleCard(
                                        schedule = schedule,
                                        today = today,
                                        onClick = {
                                            parentNavigator?.push(SessionDetailScreen(schedule))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DashboardScheduleCard(
        schedule: ScheduleSessionUiModel,
        today: LocalDate,
        onClick: () -> Unit
    ) {
        val diff = (schedule.date.toEpochDays() - today.toEpochDays()).toInt()
        val dateText = when (diff) {
            0 -> "Hôm nay"
            1 -> "Ngày mai"
            else -> "Ngày ${schedule.date.dayOfMonth.toString().padStart(2, '0')}/${schedule.date.monthNumber.toString().padStart(2, '0')}"
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
            ) {
                // Colored status indicator bar on the left
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(AppColor.Primary)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDimen.p12, vertical = AppDimen.p10),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = schedule.subjectName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            AppText(
                                text = schedule.className,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = "📍 ${schedule.room}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppText(
                            text = "⏰ $dateText, ${schedule.startTimeFormatted} - ${schedule.endTimeFormatted}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ParentDashboard() {
        Card(
            shape = RoundedCornerShape(AppDimen.p12),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimen.p16)) {
                AppText(
                    text = "Parent Portal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppText(
                    text = "Monitor your child's academic performance, check outstanding school fees (Billing) and communicate with teachers.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
