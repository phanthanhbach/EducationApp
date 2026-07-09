package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.ClassStatusFilterBottomSheet
import com.example.educationapp.domain.enums.StudentClassStatus
import com.example.educationapp.presentation.screen.feedback.ParentFeedbackScreen
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screenmodel.feedback.ParentFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_chat_24dp
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.my_classes_search_placeholder
import educationapp.shared.generated.resources.tab_feedback
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class FeedbackTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_feedback)
            val icon = painterResource(Res.drawable.ic_chat_24dp)

            return remember(title, icon) {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val parentMainScreenModel = LocalParentMainScreenModel.current
        val childrenState by parentMainScreenModel.childrenState.collectAsState()
        val selectedChild by parentMainScreenModel.selectedChild.collectAsState()

        val screenModel = koinScreenModel<ParentFeedbackScreenModel>()
        val classesState by screenModel.classesState.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()

        var showFilterSheet by remember { mutableStateOf(false) }

        val statuses = listOf(
            null to stringResource(Res.string.lb_status_all)
        ) + StudentClassStatus.entries.map { status ->
            status.name to stringResource(status.labelRes)
        }

        LaunchedEffect(selectedChild) {
            selectedChild?.let {
                screenModel.loadClasses(it.studentId.toLong())
            }
        }

        val lazyListState = rememberLazyListState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        SearchTopBarLayout(
            title = stringResource(Res.string.tab_feedback),
            searchQuery = searchQuery,
            onSearch = { screenModel.searchClasses(it) },
            lazyListState = lazyListState,
            filterIcon = Res.drawable.ic_sort_24dp,
            isFilterActive = selectedStatus != null,
            placeholder = stringResource(Res.string.my_classes_search_placeholder),
            onFilterClick = {
                showFilterSheet = true
            },
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() },
            extraContent = {
                if (childrenState is ParentChildrenState.Success) {
                    ChildSelectorBar(
                        modifier = Modifier.padding(top = AppDimen.p12),
                        children = (childrenState as ParentChildrenState.Success).children,
                        selectedChild = selectedChild,
                        onChildSelected = { parentMainScreenModel.selectChild(it) }
                    )
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) { maxScrollDp, totalHeaderHeightDp, listTopPaddingDp ->
            FeedbackListContent(
                state = classesState,
                childrenState = childrenState,
                lazyListState = lazyListState,
                maxScrollDp = maxScrollDp,
                totalHeaderHeightDp = totalHeaderHeightDp,
                listTopPaddingDp = listTopPaddingDp,
                onLoadNextPage = { screenModel.loadNextPage() },
                onRetry = {
                    selectedChild?.let {
                        screenModel.loadClasses(it.studentId.toLong())
                    }
                },
                onFeedbacksClick = { schoolClass ->
                    val studentId = selectedChild?.studentId?.toLong() ?: 0L
                    navigator.parent?.push(
                        ParentFeedbackScreen(
                            classId = schoolClass.id,
                            className = schoolClass.name,
                            studentId = studentId
                        )
                    )
                }
            )
        }

        if (showFilterSheet) {
            ClassStatusFilterBottomSheet(
                selectedStatus = selectedStatus,
                statuses = statuses,
                onStatusSelect = { screenModel.filterByStatus(it) },
                onDismissRequest = { showFilterSheet = false }
            )
        }
    }
}
