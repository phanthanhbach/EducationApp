package com.example.educationapp.presentation.screen.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
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
import com.example.educationapp.core.util.UiText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.presentation.screenmodel.feedback.ClassFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.feedback.ClassFeedbackState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.class_feedback_empty
import educationapp.shared.generated.resources.class_feedback_field_label
import educationapp.shared.generated.resources.class_feedback_field_placeholder
import educationapp.shared.generated.resources.class_feedback_send
import educationapp.shared.generated.resources.class_feedback_title
import educationapp.shared.generated.resources.class_feedback_updated_at
import educationapp.shared.generated.resources.profile_retry
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

class ClassFeedbackScreen(
    private val classId: Long,
    private val className: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ClassFeedbackScreenModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(classId) {
            screenModel.loadFeedbacks(classId)
        }

        ClassFeedbackContent(
            className = className,
            state = state,
            onBackClick = { navigator.pop() },
            onRetry = { screenModel.retry() },
            onLoadNextPage = { screenModel.loadNextPage() },
            onSubmitFeedback = screenModel::submitTeacherFeedback
        )
    }
}

@Composable
private fun ClassFeedbackContent(
    className: String,
    state: ClassFeedbackState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onLoadNextPage: () -> Unit,
    onSubmitFeedback: (StudentClassFeedback, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.class_feedback_title),
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
                is ClassFeedbackState.Loading -> {
                    ListCardSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        itemCount = 4
                    )
                }

                is ClassFeedbackState.Error -> {
                    FeedbackErrorCard(
                        message = state.message,
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimen.p16)
                    )
                }

                is ClassFeedbackState.Success -> {
                    if (state.feedbacks.isEmpty()) {
                        EmptyFeedbackCard(
                            className = className,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimen.p16)
                        )
                    } else {
                        FeedbackList(
                            state = state,
                            onLoadNextPage = onLoadNextPage,
                            onSubmitFeedback = onSubmitFeedback
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackList(
    state: ClassFeedbackState.Success,
    onLoadNextPage: () -> Unit,
    onSubmitFeedback: (StudentClassFeedback, String) -> Unit
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
        state.submitErrorMessage?.let { message ->
            item {
                AppText(
                    text = message.asString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColor.Error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(state.feedbacks, key = { "${it.classId}-${it.studentId}" }) { feedback ->
            val feedbackKey = "${feedback.classId}-${feedback.studentId}"
            StudentFeedbackCard(
                feedback = feedback,
                isSubmitting = feedbackKey in state.submittingFeedbackKeys,
                onSubmitFeedback = onSubmitFeedback
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
private fun StudentFeedbackCard(
    feedback: StudentClassFeedback,
    isSubmitting: Boolean,
    onSubmitFeedback: (StudentClassFeedback, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasTeacherFeedback = !feedback.teacherFeedback.isNullOrBlank()
    var feedbackText by remember(feedback.classId, feedback.studentId, feedback.teacherFeedback) {
        mutableStateOf(feedback.teacherFeedback.orEmpty())
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimen.p16),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = feedback.studentName.ifBlank { "#${feedback.studentId}" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AppText(
                        text = feedback.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!feedback.feedbackComment.isNullOrBlank()) {
                StudentFeedbackComment(feedback = feedback)
            }

            AppTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                label = stringResource(Res.string.class_feedback_field_label),
                labelStyle = AppTextFieldLabelStyle.External,
                placeholder = stringResource(Res.string.class_feedback_field_placeholder),
                readOnly = hasTeacherFeedback,
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                maxLength = 500
            )

            if (!feedback.teacherFeedbackDate.isNullOrBlank()) {
                AppText(
                    text = stringResource(
                        Res.string.class_feedback_updated_at,
                        formatFeedbackDate(feedback.teacherFeedbackDate)
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!hasTeacherFeedback) {
                Button(
                    onClick = { onSubmitFeedback(feedback, feedbackText) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        AppText(
                            text = stringResource(Res.string.class_feedback_send),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentFeedbackComment(
    feedback: StudentClassFeedback,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(AppDimen.p12),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = "Nhận xét của học sinh",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!feedback.feedbackRating.isNullOrBlank()) {
                AppText(
                    text = "${feedback.feedbackRating}★",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColor.Tertiary
                )
            }
        }
        AppText(
            text = feedback.feedbackComment.orEmpty(),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!feedback.feedbackAt.isNullOrBlank()) {
            AppText(
                text = formatFeedbackDate(feedback.feedbackAt),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun FeedbackErrorCard(
    message: UiText,
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
                text = message.asString(),
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
private fun EmptyFeedbackCard(
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
                text = stringResource(Res.string.class_feedback_empty, className),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatFeedbackDate(dateStr: String): String {
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
