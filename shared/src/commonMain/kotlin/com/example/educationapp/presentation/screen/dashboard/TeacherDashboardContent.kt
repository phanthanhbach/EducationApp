package com.example.educationapp.presentation.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.CalendarHelper
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.dashboard.composable.UpcomingSchedulesSection
import com.example.educationapp.presentation.screen.schedule.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_person_filled_24dp
import org.jetbrains.compose.resources.painterResource

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
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColor.Primary)
            }
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
                        AppText(text = "Thử lại", color = Color.White)
                    }
                }
            }
        }

        is TeacherDashboardState.Success -> {
            val today = remember { CalendarHelper.getCurrentDate() }

            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(AppDimen.p20)
            ) {
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
                    AppText(
                        text = "Lịch dạy 3 ngày tới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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
            }
        }
    }
}
