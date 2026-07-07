package com.example.educationapp.presentation.screen.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.rating.AppRatingBar
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.UiText
import com.example.educationapp.presentation.screenmodel.feedback.StudentFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.feedback.StudentFeedbackState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.student_feedback_teacher_section
import educationapp.shared.generated.resources.parent_feedback_title_format
import educationapp.shared.generated.resources.parent_feedback_empty
import educationapp.shared.generated.resources.parent_feedback_teacher_empty
import educationapp.shared.generated.resources.parent_feedback_teacher_date
import educationapp.shared.generated.resources.parent_feedback_student_response_title
import educationapp.shared.generated.resources.parent_feedback_student_response_date
import educationapp.shared.generated.resources.parent_feedback_student_response_empty
import org.jetbrains.compose.resources.stringResource

class ParentFeedbackScreen(
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

        ParentFeedbackContent(
            className = className,
            state = state,
            onBackClick = { navigator.pop() },
            onRetry = { screenModel.retry() }
        )
    }
}

@Composable
private fun ParentFeedbackContent(
    className: String,
    state: StudentFeedbackState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.parent_feedback_title_format, className),
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateView(
                            error = state.message,
                            onRetry = onRetry
                        )
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
                                text = stringResource(Res.string.parent_feedback_empty),
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
                            // Card 1: Teacher Feedback Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppDimen.p16),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AppText(
                                        text = stringResource(Res.string.student_feedback_teacher_section),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )

                                    AppText(
                                        text = feedback.teacherFeedback ?: stringResource(Res.string.parent_feedback_teacher_empty),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )

                                    if (!feedback.teacherFeedbackDate.isNullOrBlank()) {
                                        AppText(
                                            text = stringResource(
                                                Res.string.parent_feedback_teacher_date,
                                                formatFeedbackDate(feedback.teacherFeedbackDate)
                                            ),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }

                            // Card 2: Student Response Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                ),
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
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppText(
                                            text = stringResource(Res.string.parent_feedback_student_response_title),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )

                                        val rating = feedback.feedbackRating?.toIntOrNull() ?: 0
                                        if (rating > 0) {
                                            RatingStars(rating = rating)
                                        }
                                    }

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )

                                    if (!feedback.feedbackComment.isNullOrBlank() || (feedback.feedbackRating != null && feedback.feedbackRating != "0")) {
                                        if (!feedback.feedbackComment.isNullOrBlank()) {
                                            AppText(
                                                text = feedback.feedbackComment,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 20.sp
                                            )
                                        }

                                        if (!feedback.feedbackAt.isNullOrBlank()) {
                                            AppText(
                                                text = stringResource(
                                                    Res.string.parent_feedback_student_response_date,
                                                    formatFeedbackDate(feedback.feedbackAt)
                                                ),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    } else {
                                        AppText(
                                            text = stringResource(Res.string.parent_feedback_student_response_empty),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
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

@Composable
private fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 18.dp
) {
    AppRatingBar(
        rating = rating,
        modifier = modifier,
        maxStars = maxStars,
        starSize = starSize,
        spacing = 3.dp,
        filledColor = Color(0xFFFFB300),
        unfilledColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    )
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
