package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.layout.TopBarGreeting
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.presentation.screen.dashboard.StudentDashboardContent
import com.example.educationapp.presentation.screen.dashboard.TeacherDashboardContent
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.StudentDashboardState
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardScreenModel
import com.example.educationapp.presentation.screenmodel.dashboard.TeacherDashboardState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_dashboard_filled_24dp
import educationapp.shared.generated.resources.tab_dashboard
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

class DashboardTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_dashboard)
            val icon = painterResource(Res.drawable.ic_dashboard_filled_24dp)

            return remember(title, icon) {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val role = LocalAppRole.current
        val getMyProfileUseCase = koinInject<GetMyProfileUseCase>()
        var userName by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(role) {
            if (userName == null) {
                userName = when (val result = getMyProfileUseCase(role)) {
                    is ApiResult.Success -> {
                        result.data.fullName
                    }

                    else -> {
                        null
                    }
                }
            }
        }

        var isRefreshing by remember { mutableStateOf(false) }

        // Inject appropriate screen models early for refresh trigger and state monitoring
        val studentScreenModel =
            if (role == AppRole.STUDENT) koinScreenModel<StudentDashboardScreenModel>() else null
        val teacherScreenModel =
            if (role == AppRole.TEACHER) koinScreenModel<TeacherDashboardScreenModel>() else null

        // Sync pull to refresh state with ScreenModel loading updates
        if (studentScreenModel != null) {
            val state by studentScreenModel.state.collectAsState()
            LaunchedEffect(state) {
                if (state !is StudentDashboardState.Loading) {
                    isRefreshing = false
                }
            }
        }
        if (teacherScreenModel != null) {
            val state by teacherScreenModel.state.collectAsState()
            LaunchedEffect(state) {
                if (state !is TeacherDashboardState.Loading) {
                    isRefreshing = false
                }
            }
        }

        val onRefresh: () -> Unit = {
            isRefreshing = true
            studentScreenModel?.loadDashboardData()
            teacherScreenModel?.loadDashboardData()
        }

        val scrollState = rememberScrollState()

        AppScaffold(
            topBar = {
                AppTopBar(
                    titleContent = {
                        TopBarGreeting(userName = userName)
                    },
                    isTitleCentered = false,
                    scrollState = scrollState
                )
            },
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
            ) {
                val tabNavigator = LocalTabNavigator.current
                val onViewScheduleClick = {
                    tabNavigator.current = ScheduleTab()
                }

                // Role-based content
                when (role) {
                    AppRole.STUDENT -> {
                        StudentDashboardContent(
                            screenModel = studentScreenModel!!,
                            onViewScheduleClick = onViewScheduleClick
                        )
                    }

                    AppRole.TEACHER -> {
                        TeacherDashboardContent(
                            screenModel = teacherScreenModel!!,
                            onViewScheduleClick = onViewScheduleClick
                        )
                    }

                    else -> { /* PARENT/UNKNOWN don't use DashboardTab */
                    }
                }
            }
        }
    }
}
