package com.example.educationapp.presentation.screen.assignment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.assignment_active
import educationapp.shared.generated.resources.assignment_due_date
import educationapp.shared.generated.resources.assignment_empty
import educationapp.shared.generated.resources.assignment_final_exam
import educationapp.shared.generated.resources.assignment_inactive
import educationapp.shared.generated.resources.assignment_not_submitted_count
import educationapp.shared.generated.resources.assignment_submitted_count
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.tab_assignment
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

class ClassAssignmentsScreen(
    private val classId: Int,
    private val className: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ClassAssignmentsScreenModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(classId) {
            screenModel.loadAssignments(classId)
        }

        ClassAssignmentsContent(
            className = className,
            state = state,
            onBackClick = { navigator.pop() },
            onRetry = { screenModel.retry() },
            onLoadNextPage = { screenModel.loadNextPage() },
            onAssignmentClick = {}
        )
    }
}

@Composable
private fun ClassAssignmentsContent(
    className: String,
    state: ClassAssignmentsState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onLoadNextPage: () -> Unit,
    onAssignmentClick: (Assignment) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.tab_assignment),
                onBackClick = onBackClick
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
            when (state) {
                is ClassAssignmentsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColor.Primary)
                    }
                }

                is ClassAssignmentsState.Error -> {
                    AssignmentErrorCard(
                        message = state.message,
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimen.p16)
                    )
                }

                is ClassAssignmentsState.Success -> {
                    if (state.assignments.isEmpty()) {
                        EmptyAssignmentsCard(
                            className = className,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimen.p16)
                        )
                    } else {
                        AssignmentsList(
                            state = state,
                            onLoadNextPage = onLoadNextPage,
                            onAssignmentClick = onAssignmentClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignmentsList(
    state: ClassAssignmentsState.Success,
    onLoadNextPage: () -> Unit,
    onAssignmentClick: (Assignment) -> Unit
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
            top = AppDimen.p12,
            bottom = AppDimen.p24
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.assignments, key = { it.id }) { assignment ->
            AssignmentCard(
                assignment = assignment,
                onClick = { onAssignmentClick(assignment) }
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
private fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AppText(
                    text = assignment.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = stringResource(
                            if (assignment.active) {
                                Res.string.assignment_active
                            } else {
                                Res.string.assignment_inactive
                            }
                        ),
                        color = if (assignment.active) AppColor.Success else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (assignment.finalExam) {
                        StatusBadge(
                            text = stringResource(Res.string.assignment_final_exam),
                            color = AppColor.Primary
                        )
                    }
                }
            }

            if (!assignment.description.isNullOrBlank()) {
                AppText(
                    text = assignment.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            AppText(
                text = stringResource(Res.string.assignment_due_date, formatAssignmentDate(assignment.dueDate)),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CountPill(
                    text = stringResource(
                        Res.string.assignment_submitted_count,
                        assignment.submittedCount
                    ),
                    color = AppColor.Success,
                    modifier = Modifier.weight(1f)
                )
                CountPill(
                    text = stringResource(
                        Res.string.assignment_not_submitted_count,
                        assignment.notSubmittedCount
                    ),
                    color = AppColor.Warning,
                    modifier = Modifier.weight(1f)
                )
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
private fun CountPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AssignmentErrorCard(
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
private fun EmptyAssignmentsCard(
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
                text = stringResource(Res.string.assignment_empty, className),
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
