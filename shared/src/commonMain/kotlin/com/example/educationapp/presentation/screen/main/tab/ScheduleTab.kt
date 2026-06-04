package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.schedule.ScheduleScreenContent
import com.example.educationapp.presentation.screen.schedule.SessionDetailScreen
import com.example.educationapp.presentation.screenmodel.schedule.ScheduleScreenModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_calendar_month_filled_24dp
import educationapp.shared.generated.resources.tab_schedule
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ScheduleTab(private val role: AppRole) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_schedule)
            val icon = painterResource(Res.drawable.ic_calendar_month_filled_24dp)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ScheduleScreenModel>()
        val selectedDate by screenModel.selectedDate.collectAsState()
        val isMonthExpanded by screenModel.isMonthExpanded.collectAsState()
        val schedulesState by screenModel.schedulesState.collectAsState()
        val filteredSchedules by screenModel.filteredSchedules.collectAsState()
        val highlightDates by screenModel.highlightDates.collectAsState()

        ScheduleScreenContent(
            role = role,
            selectedDate = selectedDate,
            isMonthExpanded = isMonthExpanded,
            schedulesState = schedulesState,
            filteredSchedules = filteredSchedules,
            highlightDates = highlightDates,
            onSessionClick = { session ->
                navigator.parent?.push(SessionDetailScreen(session))
            },
            onDateSelected = { screenModel.selectedDate.value = it },
            onToggleExpand = { screenModel.isMonthExpanded.value = !screenModel.isMonthExpanded.value },
            onRetry = { screenModel.retryLoad() }
        )
    }
}
