package com.example.educationapp.presentation.screen.assignment.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppButton
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.domain.entity.SubmissionDetail
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentSubmissionsState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.grade_sheet_comment_label
import educationapp.shared.generated.resources.grade_sheet_comment_placeholder
import educationapp.shared.generated.resources.grade_sheet_score_invalid
import educationapp.shared.generated.resources.grade_sheet_score_label
import educationapp.shared.generated.resources.grade_sheet_score_placeholder
import educationapp.shared.generated.resources.grade_sheet_submit_btn
import educationapp.shared.generated.resources.grade_sheet_title
import educationapp.shared.generated.resources.submission_empty
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubmissionsList(
    state: AssignmentSubmissionsState.Success,
    onLoadNextPage: () -> Unit,
    onGradeClick: (SubmissionDetail) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(AppDimen.p16)
) {
    if (state.submissions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            AppText(
                text = stringResource(Res.string.submission_empty),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

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
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
    ) {
        items(state.submissions, key = { "${it.studentId}_${it.assignmentId}" }) { submission ->
            SubmissionCard(
                submission = submission,
                onGradeClick = { onGradeClick(submission) }
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
                        modifier = Modifier.size(AppDimen.p24),
                        strokeWidth = AppDimen.p2,
                        color = AppColor.Primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeSubmissionSheet(
    submission: SubmissionDetail,
    isLoading: Boolean,
    errorMessage: String?,
    onDismissRequest: () -> Unit,
    onGradeSubmit: (score: Double, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var scoreText by remember { mutableStateOf(submission.score?.toString() ?: "") }
    var commentText by remember { mutableStateOf(submission.teacherComment ?: "") }

    val scoreDouble = scoreText.toDoubleOrNull()
    val canSubmit =
        scoreText.isNotBlank() && scoreDouble != null && scoreDouble in 0.0..10.0 && !isLoading

    val scoreError = when {
        scoreText.isNotBlank() && scoreDouble == null -> stringResource(Res.string.grade_sheet_score_invalid)
        scoreText.isNotBlank() && (scoreDouble != null && (scoreDouble !in 0.0..10.0)) -> stringResource(
            Res.string.grade_sheet_score_invalid
        )

        else -> null
    }

    AppBottomSheet(
        onDismissRequest = onDismissRequest,
        title = stringResource(Res.string.grade_sheet_title),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.p16)
                .padding(bottom = AppDimen.p24),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppTextField(
                value = scoreText,
                onValueChange = { scoreText = it },
                label = stringResource(Res.string.grade_sheet_score_label),
                labelStyle = AppTextFieldLabelStyle.External,
                placeholder = stringResource(Res.string.grade_sheet_score_placeholder),
                errorMessage = scoreError ?: errorMessage,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = stringResource(Res.string.grade_sheet_comment_label),
                labelStyle = AppTextFieldLabelStyle.External,
                placeholder = stringResource(Res.string.grade_sheet_comment_placeholder),
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            AppButton(
                text = stringResource(Res.string.grade_sheet_submit_btn),
                onClick = {
                    if (canSubmit) {
                        onGradeSubmit(scoreDouble, commentText)
                    }
                },
                enabled = canSubmit,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}