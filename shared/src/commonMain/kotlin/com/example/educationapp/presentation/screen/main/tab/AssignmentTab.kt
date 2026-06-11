package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screen.feedback.ClassFeedbackScreen
import com.example.educationapp.presentation.screen.assignment.ClassAssignmentsScreen
import com.example.educationapp.presentation.screen.assignment.StudentClassAssignmentsScreen
import com.example.educationapp.presentation.screen.my_classes.MyClassScreenContent
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabScreenModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_assignment_filled_24dp
import educationapp.shared.generated.resources.tab_assignment
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AssignmentTab : Tab {

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
        val role = LocalAppRole.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AssignmentTabScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()

        LaunchedEffect(role) {
            screenModel.loadProfileAndClasses(role)
        }

        MyClassScreenContent(
            role = role,
            state = state,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            onSearch = { query -> screenModel.searchClasses(query) },
            onStatusSelect = { status -> screenModel.filterByStatus(status) },
            onAssignmentsClick = { classId, className ->
                if (role == AppRole.STUDENT) {
                    navigator.parent?.push(StudentClassAssignmentsScreen(classId.toInt(), className))
                } else {
                    navigator.parent?.push(ClassAssignmentsScreen(classId.toInt(), className))
                }
            },
            onFeedbacksClick = { classId, className ->
                navigator.parent?.push(ClassFeedbackScreen(classId, className))
            },
            onLoadNextPage = { screenModel.loadNextPage() },
            onRetry = { screenModel.loadProfileAndClasses() }
        )
    }
}
