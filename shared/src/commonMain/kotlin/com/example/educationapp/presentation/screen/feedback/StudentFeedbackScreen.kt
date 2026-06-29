package com.example.educationapp.presentation.screen.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.rating.AppRatingBar
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.domain.entity.StudentClassFeedback
import com.example.educationapp.presentation.screenmodel.feedback.StudentFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.feedback.StudentFeedbackState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_star_24dp
import educationapp.shared.generated.resources.ic_star_filled_24dp
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.student_feedback_comment_label
import educationapp.shared.generated.resources.student_feedback_comment_placeholder
import educationapp.shared.generated.resources.student_feedback_empty
import educationapp.shared.generated.resources.student_feedback_rating_label
import educationapp.shared.generated.resources.student_feedback_submit
import educationapp.shared.generated.resources.student_feedback_submitted
import educationapp.shared.generated.resources.student_feedback_submitted_at
import educationapp.shared.generated.resources.student_feedback_teacher_section
import educationapp.shared.generated.resources.student_feedback_title
import educationapp.shared.generated.resources.student_feedback_title_format
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class StudentFeedbackScreen(
    private val classId: Long,
    private val className: String,
    private val studentId: Long
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<StudentFeedbackScreenModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(studentId, classId) {
            screenModel.loadFeedback(studentId, classId)
        }

        StudentFeedbackContent(
            className = className,
            classId = classId,
            state = state,
            onBackClick = { navigator.pop() },
            onRetry = { screenModel.retry() },
            onSubmitFeedback = { rating, comment ->
                screenModel.submitFeedback(classId, rating, comment)
            }
        )
    }
}

@Composable
private fun StudentFeedbackContent(
    className: String,
    classId: Long,
    state: StudentFeedbackState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onSubmitFeedback: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.student_feedback_title_format, className),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is StudentFeedbackState.Loading -> {
                    ListCardSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        itemCount = 4
                    )
                }

                is StudentFeedbackState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimen.p24)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppText(
                            text = state.message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
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

                is StudentFeedbackState.Success -> {
                    val feedback = state.feedback
                    if (feedback == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppDimen.p24),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = stringResource(Res.string.student_feedback_empty),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(AppDimen.p16),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppDimen.p16),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val hasTeacherFeedback = !feedback.teacherFeedback.isNullOrBlank()
                                    val hasStudentFeedback = !feedback.feedbackRating.isNullOrBlank() && feedback.feedbackRating != "0"

                                    // 1. Teacher Feedback Display (if any) at the top of the card
                                    if (hasTeacherFeedback) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .padding(AppDimen.p12),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AppText(
                                                text = stringResource(Res.string.student_feedback_teacher_section),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            AppText(
                                                text = feedback.teacherFeedback.orEmpty(),
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            if (!feedback.teacherFeedbackDate.isNullOrBlank()) {
                                                AppText(
                                                    text = formatFeedbackDate(feedback.teacherFeedbackDate),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }

                                    // Divider between Teacher and Student Feedbacks
                                    if (hasTeacherFeedback) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                    }

                                    // 2. Student Feedback Form or Display at the bottom of the card
                                    if (hasStudentFeedback) {
                                        val rating = feedback.feedbackRating.toIntOrNull() ?: 5
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            AppText(
                                                text = stringResource(Res.string.student_feedback_submitted),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            AppRatingBar(
                                                rating = rating,
                                                onRatingChanged = null,
                                                starSize = 22.dp
                                            )

                                            if (!feedback.feedbackComment.isNullOrBlank()) {
                                                AppText(
                                                    text = feedback.feedbackComment,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            if (!feedback.feedbackAt.isNullOrBlank()) {
                                                AppText(
                                                    text = stringResource(
                                                        Res.string.student_feedback_submitted_at,
                                                        formatFeedbackDate(feedback.feedbackAt)
                                                    ),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    } else {
                                        // Feedback Input Form
                                        var ratingInput by remember { mutableStateOf(5) }
                                        var commentInput by remember { mutableStateOf("") }

                                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                AppText(
                                                    text = stringResource(Res.string.student_feedback_rating_label),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                AppRatingBar(
                                                    rating = ratingInput,
                                                    onRatingChanged = { ratingInput = it },
                                                    starSize = 32.dp
                                                )
                                            }

                                            AppTextField(
                                                value = commentInput,
                                                onValueChange = { commentInput = it },
                                                label = stringResource(Res.string.student_feedback_comment_label),
                                                labelStyle = AppTextFieldLabelStyle.External,
                                                placeholder = stringResource(Res.string.student_feedback_comment_placeholder),
                                                singleLine = false,
                                                minLines = 3,
                                                maxLines = 5,
                                                maxLength = 500,
                                                enabled = !state.isSubmitting
                                            )

                                            if (state.submitError != null) {
                                                AppText(
                                                    text = state.submitError,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }

                                            Button(
                                                onClick = { onSubmitFeedback(ratingInput, commentInput) },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = !state.isSubmitting && ratingInput > 0,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = AppColor.Primary
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                if (state.isSubmitting) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    AppText(
                                                        text = stringResource(Res.string.student_feedback_submit),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
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
