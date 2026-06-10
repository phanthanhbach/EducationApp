package com.example.educationapp.presentation.screen.assignment

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.presentation.screenmodel.assignment.StudentClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.StudentClassAssignmentsState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_docs_24dp
import educationapp.shared.generated.resources.ic_error_outline_24dp
import educationapp.shared.generated.resources.ic_check_circle_24dp
import educationapp.shared.generated.resources.ic_upload_24dp
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.tab_assignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlin.time.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

class StudentClassAssignmentsScreen(
    private val classId: Int,
    private val className: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<StudentClassAssignmentsScreenModel>()
        val state by screenModel.state.collectAsState()
        val submittedFilter by screenModel.submittedFilter.collectAsState()

        var toastMessage by remember { mutableStateOf<String?>(null) }

        // Auto dismiss toast
        LaunchedEffect(toastMessage) {
            if (toastMessage != null) {
                delay(2500.milliseconds)
                toastMessage = null
            }
        }

        LaunchedEffect(classId) {
            screenModel.init(classId)
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AppTopBar(
                    title = className,
                    onBackClick = { navigator.pop() }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p8),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppChip(
                            text = "Chưa nộp",
                            selected = !submittedFilter,
                            onClick = { screenModel.setSubmittedFilter(false) }
                        )
                        AppChip(
                            text = "Đã nộp",
                            selected = submittedFilter,
                            onClick = { screenModel.setSubmittedFilter(true) }
                        )
                    }

                    // Content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (val currentState = state) {
                            is StudentClassAssignmentsState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = AppColor.Primary)
                                }
                            }

                            is StudentClassAssignmentsState.Error -> {
                                StudentAssignmentErrorCard(
                                    message = currentState.message,
                                    onRetry = { screenModel.retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppDimen.p16)
                                )
                            }

                            is StudentClassAssignmentsState.Success -> {
                                if (currentState.assignments.isEmpty()) {
                                    EmptyStudentAssignmentsCard(
                                        className = className,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(AppDimen.p16)
                                    )
                                } else {
                                    StudentAssignmentsList(
                                        state = currentState,
                                        onLoadNextPage = { screenModel.loadNextPage() },
                                        onShowToast = { msg -> toastMessage = msg }
                                    )
                                }
                            }
                        }
                    }
                }

                // Toast message
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
                        AppText(
                            text = toastMessage ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.padding(horizontal = AppDimen.p16, vertical = AppDimen.p12),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentAssignmentsList(
    state: StudentClassAssignmentsState.Success,
    onLoadNextPage: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .filter { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                lastVisibleItem != null && lastVisibleItem.index >= lazyListState.layoutInfo.totalItemsCount - 3
            }
            .collect {
                onLoadNextPage()
            }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AppDimen.p16,
            end = AppDimen.p16,
            top = AppDimen.p8,
            bottom = AppDimen.p24
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.assignments, key = { it.assignmentId }) { assignment ->
            StudentAssignmentCard(
                assignment = assignment,
                onShowToast = onShowToast
            )
        }

        if (state.hasNextPage) {
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

@Composable
private fun StudentAssignmentCard(
    assignment: StudentAssignment,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val isOverdue = remember(assignment.dueDate) {
        try {
            val dueInstant = Instant.parse(assignment.dueDate)
            dueInstant < Clock.System.now()
        } catch (e: Exception) {
            false
        }
    }

    val isWarning = isOverdue && !assignment.submitted

    // Premium styling colors matching mockup and theme
    val borderStrokeColor = if (isWarning) {
        Color(0xFFFFCDD2) // Light red border
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }

    val containerColor = if (isWarning) {
        Color(0xFFFFEBEE).copy(alpha = 0.6f) // Very light red background
    } else {
        MaterialTheme.colorScheme.surface
    }

    val iconBgColor = when {
        isWarning -> Color(0xFFFFEBEE)
        assignment.submitted -> Color(0xFFE8F5E9)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val iconColor = when {
        isWarning -> Color(0xFFD32F2F)
        assignment.submitted -> Color(0xFF388E3C)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconRes = when {
        isWarning -> Res.drawable.ic_error_outline_24dp
        assignment.submitted -> Res.drawable.ic_check_circle_24dp
        else -> Res.drawable.ic_docs_24dp
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon status indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Description column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppText(
                    text = assignment.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!assignment.description.isNullOrBlank()) {
                    AppText(
                        text = assignment.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Due date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppText(
                        text = "Due ${formatAssignmentDate(assignment.dueDate)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isWarning) {
                        AppText(
                            text = "Overdue",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    StatusBadge(
                        text = if (assignment.submitted) "Submitted" else "Not submitted",
                        color = if (assignment.submitted) AppColor.Success else AppColor.Warning
                    )

                    // Final Exam Badge
                    if (assignment.finalExam) {
                        StatusBadge(
                            text = "Final exam",
                            color = Color(0xFF7E57C2) // Purple color
                        )
                    }

                    // Score Badge
                    if (assignment.score != null) {
                        StatusBadge(
                            text = "Score: ${assignment.score}",
                            color = AppColor.Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Assignment brief Link
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                if (!assignment.assignmentFileAttachment.isNullOrBlank()) {
                                    try {
                                        val url = if (assignment.assignmentFileAttachment.startsWith("http://") || 
                                            assignment.assignmentFileAttachment.startsWith("https://")) {
                                            assignment.assignmentFileAttachment
                                        } else {
                                            "http://${assignment.assignmentFileAttachment}"
                                        }
                                        uriHandler.openUri(url)
                                    } catch (e: Exception) {
                                        // Swallow error silently so that no error is shown in the app
                                    }
                                }
                            }
                            .padding(vertical = 6.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_docs_24dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        AppText(
                            text = "Assignment brief",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Submit or View Submission button
                    if (!assignment.submitted) {
                        Button(
                            onClick = {
                                onShowToast("Tính năng nộp bài đang được phát triển.")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFE65100), Color(0xFFC2185B))
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_upload_24dp),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                AppText(
                                    text = "Submit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else if (!assignment.fileAttachment.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val url = if (assignment.fileAttachment.startsWith("http://") || 
                                        assignment.fileAttachment.startsWith("https://")) {
                                        assignment.fileAttachment
                                    } else {
                                        "http://${assignment.fileAttachment}"
                                    }
                                    uriHandler.openUri(url)
                                } catch (e: Exception) {
                                    // Swallow silently
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_docs_24dp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                AppText(
                                    text = "View submission",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
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
private fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        AppText(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun StudentAssignmentErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, AppColor.Error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppDimen.p16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppText(
                text = message,
                fontSize = 14.sp,
                color = AppColor.Error
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
            ) {
                AppText(
                    text = stringResource(Res.string.profile_retry),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun EmptyStudentAssignmentsCard(
    className: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p24),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = "Lớp $className chưa có bài tập nào.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatAssignmentDate(dateStr: String): String {
    if (dateStr.isBlank()) return "--/--/----"
    return try {
        val parts = dateStr.split('T')
        val datePart = parts[0]
        val datePieces = datePart.split('-')
        val formattedDate = if (datePieces.size == 3) {
            "${datePieces[2]}/${datePieces[1]}/${datePieces[0]}"
        } else {
            datePart
        }

        val timePart = parts.getOrNull(1)?.take(5)
        if (!timePart.isNullOrBlank()) "$formattedDate $timePart" else formattedDate
    } catch (e: Exception) {
        dateStr
    }
}
