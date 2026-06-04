package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.ic_close_24dp
import educationapp.shared.generated.resources.ic_search_24dp
import educationapp.shared.generated.resources.tab_assignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AssignmentTab(private val role: AppRole) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_assignment)
            val icon = painterResource(Res.drawable.ic_assignment_filled_24dp)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(AppDimen.p16)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                AppText(
                    text = stringResource(Res.string.tab_assignment),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (role == AppRole.TEACHER) {
                    TeacherClassesScreen()
                } else {
                    val displayRoleText = when (role) {
                        AppRole.STUDENT -> "Your Assignments & Tasks"
                        AppRole.PARENT -> "Children Assignments Tracker"
                        else -> "Assignments"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(AppDimen.p16)) {
                            AppText(
                                text = displayRoleText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppText(
                                text = "No active homework or assignment updates.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun TeacherClassesScreen() {
        val screenModel = koinScreenModel<AssignmentTabScreenModel>()
        val state by screenModel.state.collectAsState()

        var localSearchQuery by remember { mutableStateOf(screenModel.searchQuery) }
        var toastMessage by remember { mutableStateOf<String?>(null) }

        // Auto dismiss toast
        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                delay(2500)
                toastMessage = null
            }
        }

        // Debounce search query changes
        LaunchedEffect(localSearchQuery) {
            delay(500)
            if (localSearchQuery != screenModel.searchQuery) {
                screenModel.searchClasses(localSearchQuery)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                // Search Input
                AppTextField(
                    value = localSearchQuery,
                    onValueChange = { localSearchQuery = it },
                    placeholder = "Tìm kiếm theo tên lớp, khóa học...",
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_search_24dp),
                            contentDescription = "Search icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (localSearchQuery.isNotEmpty()) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_close_24dp),
                                contentDescription = "Clear search",
                                modifier = Modifier.clickable { localSearchQuery = "" },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Filter chips row
                val statuses = listOf(
                    "ALL" to "Tất cả",
                    "UPCOMING" to "Sắp diễn ra",
                    "ONGOING" to "Đang giảng dạy",
                    "COMPLETED" to "Đã kết thúc",
                    "CANCELLED" to "Đã hủy"
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    statuses.forEach { (statusKey, statusLabel) ->
                        val isSelected = if (statusKey == "ALL") {
                            screenModel.selectedStatus == null
                        } else {
                            screenModel.selectedStatus == statusKey
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                screenModel.filterByStatus(statusKey)
                            },
                            label = {
                                AppText(
                                    text = statusLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                // Content space
                when (val currentState = state) {
                    is AssignmentTabState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColor.Primary)
                        }
                    }

                    is AssignmentTabState.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppDimen.p16),
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
                                    onClick = { screenModel.loadProfileAndClasses() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                                ) {
                                    AppText(text = "Thử lại", color = Color.White)
                                }
                            }
                        }
                    }

                    is AssignmentTabState.Success -> {
                        if (currentState.classes.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = AppDimen.p16),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.3f
                                    )
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppDimen.p24),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppText(
                                        text = "Không tìm thấy lớp học nào phù hợp.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            val lazyListState = rememberLazyListState()

                            LaunchedEffect(lazyListState) {
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
                                    .filter { visibleItems ->
                                        val lastVisibleItem = visibleItems.lastOrNull()
                                        lastVisibleItem != null && lastVisibleItem.index >= lazyListState.layoutInfo.totalItemsCount - 3
                                    }
                                    .collect {
                                        screenModel.loadNextPage()
                                    }
                            }

                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(currentState.classes) { index, schoolClass ->
                                    ClassCard(
                                        schoolClass = schoolClass,
                                        onAssignmentsClick = {
                                            toastMessage =
                                                "Tính năng quản lý Bài tập lớp ${schoolClass.name} đang được phát triển."
                                        },
                                        onFeedbacksClick = {
                                            toastMessage =
                                                "Tính năng xem Phản hồi lớp ${schoolClass.name} đang được phát triển."
                                        }
                                    )
                                }

                                if (currentState.hasNextPage) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = AppDimen.p16),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp,
                                                color = AppColor.Primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Toast overlay
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
                    .padding(bottom = AppDimen.p24, start = AppDimen.p24, end = AppDimen.p24)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = AppDimen.p16,
                            vertical = AppDimen.p12
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppText(
                            text = toastMessage ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ClassCard(
        schoolClass: com.example.educationapp.domain.entity.SchoolClass,
        onAssignmentsClick: () -> Unit,
        onFeedbacksClick: () -> Unit
    ) {
        val statusColor = when (schoolClass.status) {
            "UPCOMING" -> Color(0xFF2196F3) // Professional Blue
            "ONGOING" -> Color(0xFF4CAF50)  // Success Green
            "COMPLETED" -> Color(0xFF9E9E9E) // Grey
            "CANCELLED" -> Color(0xFFF44336) // Red
            else -> AppColor.Primary
        }

        val statusText = when (schoolClass.status) {
            "UPCOMING" -> "Sắp diễn ra"
            "ONGOING" -> "Đang giảng dạy"
            "COMPLETED" -> "Đã hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> schoolClass.status
        }

        val formattedStartDate = formatDate(schoolClass.startDate)
        val formattedEndDate = formatDate(schoolClass.endDate)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(statusColor)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimen.p16),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = schoolClass.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            AppText(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AppText(
                            text = "📚 Khóa học: ${schoolClass.courseName}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppText(
                            text = "🏢 Chi nhánh: ${schoolClass.branchName}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppText(
                            text = "📅 Thời gian: $formattedStartDate - $formattedEndDate",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val ratio = if (schoolClass.maxStudents > 0) {
                        schoolClass.currentStudents.toFloat() / schoolClass.maxStudents.toFloat()
                    } else {
                        0f
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = "Sĩ số học viên",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AppText(
                                text = "${schoolClass.currentStudents}/${schoolClass.maxStudents} học viên",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        LinearProgressIndicator(
                            progress = ratio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AppColor.Primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onFeedbacksClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            AppText(
                                text = "Phản hồi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = onAssignmentsClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            AppText(
                                text = "Bài tập",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return "--/--/----"
        return try {
            val datePart = if (dateStr.contains('T')) {
                dateStr.split('T')[0]
            } else {
                dateStr
            }
            val parts = datePart.split('-')
            if (parts.size == 3) {
                val year = parts[0]
                val month = parts[1]
                val day = parts[2]
                "$day/$month/$year"
            } else {
                dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }
}
