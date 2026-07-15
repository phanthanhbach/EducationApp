package com.example.educationapp.presentation.screen.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.shimmer.skeleton.AssignmentCardSkeleton
import com.example.educationapp.domain.entity.Assignment
import com.example.educationapp.presentation.screen.assignment.composable.AssignmentsList
import com.example.educationapp.presentation.screen.assignment.composable.EmptyAssignmentsCard
import com.example.educationapp.presentation.screen.main.LocalIsTablet
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsScreenModel
import com.example.educationapp.presentation.screenmodel.assignment.ClassAssignmentsState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.tab_assignment
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
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        LaunchedEffect(classId) {
            screenModel.loadAssignments(classId)
        }

        ClassAssignmentsContent(
            className = className,
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() },
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
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onLoadNextPage: () -> Unit,
    onAssignmentClick: (Assignment) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTablet = LocalIsTablet.current
    val screenPadding = if (isTablet) AppDimen.p24 else AppDimen.p16

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.tab_assignment),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state) {
                is ClassAssignmentsState.Loading -> {
                    AssignmentCardSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenPadding),
                        itemCount = 3
                    )
                }

                is ClassAssignmentsState.Error -> {
                    ErrorStateView(
                        error = state.message,
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(screenPadding)
                    )
                }

                is ClassAssignmentsState.Success -> {
                    if (state.assignments.isEmpty()) {
                        EmptyAssignmentsCard(
                            className = className,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(screenPadding)
                        )
                    } else {
                        AssignmentsList(
                            state = state,
                            onLoadNextPage = onLoadNextPage,
                            onAssignmentClick = onAssignmentClick,
                            contentPadding = PaddingValues(
                                start = screenPadding,
                                end = screenPadding,
                                top = AppDimen.p12,
                                bottom = AppDimen.p24
                            )
                        )
                    }
                }
            }
        }
    }
}
