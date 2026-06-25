package com.example.educationapp.presentation.screen.assignment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.file.UploadFile
import com.example.educationapp.core.file.rememberUploadFilePicker
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.shimmer.skeleton.AssignmentCardSkeleton
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.layout.LocalTopBarHazeState
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.upload.UploadReviewDialog
import com.example.educationapp.domain.entity.StudentAssignment
import com.example.educationapp.presentation.screen.assignment.composable.StudentAssignmentCard
import com.example.educationapp.presentation.screenmodel.assignment.StudentClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.StudentClassAssignmentsState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.assignment_submitted
import educationapp.shared.generated.resources.assignment_not_submitted
import educationapp.shared.generated.resources.assignment_dialog_submit_title
import educationapp.shared.generated.resources.assignment_dialog_submit_desc
import educationapp.shared.generated.resources.assignment_empty
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
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
        val submittingAssignmentIds by screenModel.submittingAssignmentIds.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        var toastMessage by remember { mutableStateOf<String?>(null) }
        var uploadAssignment by remember { mutableStateOf<StudentAssignment?>(null) }
        var selectedUploadFile by remember { mutableStateOf<UploadFile?>(null) }
        val uploadFilePicker = rememberUploadFilePicker(
            onFileSelected = { file -> selectedUploadFile = file },
            onError = { message -> toastMessage = message }
        )

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

        AppScaffold(
            topBar = {
                CompositionLocalProvider(LocalTopBarHazeState provides null) {
                    AppTopBar(
                        title = className,
                        onBackClick = { navigator.pop() }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = AppDimen.p16, vertical = AppDimen.p8),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppChip(
                            text = stringResource(Res.string.assignment_not_submitted),
                            selected = !submittedFilter,
                            onClick = { screenModel.setSubmittedFilter(false) }
                        )
                        AppChip(
                            text = stringResource(Res.string.assignment_submitted),
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
                                AssignmentCardSkeleton(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    itemCount = 3
                                )
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
                                        submittingAssignmentIds = submittingAssignmentIds,
                                        onLoadNextPage = { screenModel.loadNextPage() },
                                        onSubmitAssignmentClick = { assignment ->
                                            uploadAssignment = assignment
                                            selectedUploadFile = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                uploadAssignment?.let { assignment ->
                    val isSubmitting = assignment.assignmentId in submittingAssignmentIds
                    UploadReviewDialog(
                        title = stringResource(Res.string.assignment_dialog_submit_title),
                        description = stringResource(Res.string.assignment_dialog_submit_desc, assignment.title),
                        selectedFile = selectedUploadFile,
                        isSubmitting = isSubmitting,
                        onChooseFile = { uploadFilePicker.launch() },
                        onSubmit = {
                            val file = selectedUploadFile ?: return@UploadReviewDialog
                            screenModel.submitAssignment(assignment, file) { success, message ->
                                toastMessage = message
                                if (success) {
                                    uploadAssignment = null
                                    selectedUploadFile = null
                                }
                            }
                        },
                        onDismiss = {
                            uploadAssignment = null
                            selectedUploadFile = null
                        }
                    )
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
                            modifier = Modifier.padding(
                                horizontal = AppDimen.p16,
                                vertical = AppDimen.p12
                            ),
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
    submittingAssignmentIds: Set<Int>,
    onLoadNextPage: () -> Unit,
    onSubmitAssignmentClick: (StudentAssignment) -> Unit
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
                isSubmitting = assignment.assignmentId in submittingAssignmentIds,
                onSubmitAssignmentClick = { onSubmitAssignmentClick(assignment) }
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
                text = stringResource(Res.string.assignment_empty, className),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

