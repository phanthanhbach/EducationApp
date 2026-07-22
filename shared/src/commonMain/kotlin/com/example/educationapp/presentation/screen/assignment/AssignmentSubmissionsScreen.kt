package com.example.educationapp.presentation.screen.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.theme.screenPadding
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.shimmer.skeleton.AssignmentCardSkeleton
import com.example.educationapp.presentation.screen.assignment.composable.SubmissionFilterBar
import com.example.educationapp.presentation.screen.assignment.composable.SubmissionsList
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentSubmissionsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentSubmissionsState
import com.example.educationapp.presentation.screenmodel.assignment.GradeDialogState
import com.example.educationapp.presentation.screen.assignment.composable.GradeSubmissionSheet
import com.example.educationapp.domain.entity.SubmissionDetail
import com.example.educationapp.domain.enums.AssignmentFilter

class AssignmentSubmissionsScreen(
    private val assignmentId: Int,
    private val classId: Int,
    private val assignmentTitle: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AssignmentSubmissionsScreenModel>()
        val state by screenModel.state.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()
        val selectedFilter by screenModel.submittedFilter.collectAsState()
        val gradeState by screenModel.gradeState.collectAsState()

        LaunchedEffect(assignmentId, classId) {
            screenModel.loadSubmissions(assignmentId, classId)
        }

        AssignmentSubmissionsContent(
            assignmentTitle = assignmentTitle,
            state = state,
            selectedFilter = selectedFilter,
            isRefreshing = isRefreshing,
            onFilterSelected = { screenModel.setFilter(it) },
            onBackClick = { navigator.pop() },
            onRetry = { screenModel.retry() },
            onLoadNextPage = { screenModel.loadNextPage() },
            onRefresh = { screenModel.refreshData() },
            onGradeClick = { screenModel.showGradeDialog(it) }
        )

        (gradeState as? GradeDialogState.Visible)?.let { visibleState ->
            GradeSubmissionSheet(
                submission = visibleState.submission,
                isLoading = visibleState.isLoading,
                errorMessage = visibleState.errorMessage?.asString(),
                onDismissRequest = { screenModel.dismissGradeDialog() },
                onGradeSubmit = { score, comment ->
                    screenModel.gradeSubmission(score, comment)
                }
            )
        }
    }
}

@Composable
private fun AssignmentSubmissionsContent(
    assignmentTitle: String,
    state: AssignmentSubmissionsState,
    selectedFilter: AssignmentFilter,
    isRefreshing: Boolean,
    onFilterSelected: (AssignmentFilter) -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onLoadNextPage: () -> Unit,
    onRefresh: () -> Unit,
    onGradeClick: (SubmissionDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    val paddingHorizontal = AppDimen.screenPadding

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = assignmentTitle,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filter Bar
            SubmissionFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (state) {
                    is AssignmentSubmissionsState.Loading -> {
                        AssignmentCardSkeleton(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingHorizontal),
                            itemCount = 4
                        )
                    }

                    is AssignmentSubmissionsState.Error -> {
                        ErrorStateView(
                            error = state.message,
                            onRetry = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingHorizontal)
                        )
                    }

                    is AssignmentSubmissionsState.Success -> {
                        SubmissionsList(
                            state = state,
                            onLoadNextPage = onLoadNextPage,
                            onGradeClick = onGradeClick,
                            contentPadding = PaddingValues(
                                start = paddingHorizontal,
                                end = paddingHorizontal,
                                top = AppDimen.p8,
                                bottom = AppDimen.p24
                            )
                        )
                    }
                }
            }
        }
    }
}
