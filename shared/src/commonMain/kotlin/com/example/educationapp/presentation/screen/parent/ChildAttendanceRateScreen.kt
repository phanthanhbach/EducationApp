package com.example.educationapp.presentation.screen.parent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import com.example.educationapp.core.ui.shimmer.skeleton.InfoRowSkeleton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.AttendanceRate
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.presentation.screen.parent.component.ClassChipsRow
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateState
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class ChildAttendanceRateScreen(
    private val studentId: Long,
    private val studentName: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ChildAttendanceRateScreenModel> { parametersOf(studentId) }

        val state by screenModel.state.collectAsState()
        val selectedClass by screenModel.selectedClass.collectAsState()

        val scrollState = rememberScrollState()

        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Tỉ lệ chuyên cần - $studentName",
                    onBackClick = { navigator.pop() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    isTitleCentered = false
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when (val currentState = state) {
                    is ChildAttendanceRateState.Loading -> {
                        InfoRowSkeleton(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            rowCount = 5,
                            showIcons = true
                        )
                    }

                    is ChildAttendanceRateState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AppText(
                                    text = currentState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp
                                )
                                // Add a retry mechanism
                            }
                        }
                    }

                    is ChildAttendanceRateState.Success -> {
                        val classes = currentState.classes
                        val rates = currentState.rates

                        if (classes.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = "Con chưa tham gia lớp học nào.",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                // 1. Summary Card
                                SummaryCard(
                                    total = currentState.summaryTotal,
                                    attended = currentState.summaryAttended,
                                    absent = currentState.summaryAbsent,
                                    rate = currentState.summaryRate
                                )

                                // Section title
                                AppText(
                                    text = "Chi tiết theo lớp học",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                                )

                                // 2. Class Chips Row
                                ClassChipsRow(
                                    classes = classes,
                                    selectedClass = selectedClass,
                                    onClassSelected = { screenModel.selectClass(it) }
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // 3. Selected Class Detail
                                selectedClass?.let { schoolClass ->
                                    val classRate = rates[schoolClass.id]
                                    ClassDetailSection(
                                        schoolClass = schoolClass,
                                        rate = classRate
                                    )
                                } ?: Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppText(
                                        text = "Vui lòng chọn một lớp học để xem chi tiết.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    private fun SummaryCard(
        total: Int,
        attended: Int,
        absent: Int,
        rate: Double
    ) {
        val gradientBrush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
            )
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(gradientBrush)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppText(
                            text = "Tổng kết chuyên cần",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                AppText(
                                    text = "Tổng số buổi học: $total",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                AppText(
                                    text = "Đã điểm danh: $attended",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF44336))
                                )
                                AppText(
                                    text = "Vắng mặt: $absent",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    AttendanceProgressRing(
                        rate = rate,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ClassDetailSection(
        schoolClass: SchoolClass,
        rate: AttendanceRate?
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppText(
                        text = schoolClass.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AppText(
                        text = "Môn học: ${schoolClass.courseName}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        text = "Giáo viên: ${schoolClass.teacherName}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                if (rate != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppText(
                                text = "Số buổi lớp học: ${rate.totalSessions}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppText(
                                    text = "Đã đi học:",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AppText(
                                    text = "${rate.attendedSessions} buổi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppText(
                                    text = "Vắng mặt:",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AppText(
                                    text = "${rate.totalSessions - rate.attendedSessions} buổi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF44336)
                                )
                            }
                        }

                        AttendanceProgressRing(
                            rate = rate.attendanceRate,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText(
                            text = "Không có dữ liệu tỉ lệ chuyên cần cho lớp học này.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AttendanceProgressRing(
        rate: Double,
        modifier: Modifier = Modifier,
        size: Dp = 90.dp,
        strokeWidth: Dp = 8.dp
    ) {
        Box(
            modifier = modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { (rate / 100.0).coerceIn(0.0..1.0).toFloat() },
                modifier = Modifier.fillMaxSize(),
                color = when {
                    rate >= 80 -> Color(0xFF4CAF50)
                    rate >= 50 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                },
                strokeWidth = strokeWidth,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val roundedRate = (rate * 10).roundToInt() / 10.0
                AppText(
                    text = "$roundedRate%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AppText(
                    text = "Tỉ lệ",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
