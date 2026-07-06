package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.ClassStatusFilterBottomSheet
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.presentation.screen.feedback.ParentFeedbackScreen
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.my_classes.ClassCard
import com.example.educationapp.presentation.screenmodel.feedback.FeedbackClassesState
import com.example.educationapp.presentation.screenmodel.feedback.ParentFeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_chat_24dp
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.lb_status_dropped
import educationapp.shared.generated.resources.my_classes_empty
import educationapp.shared.generated.resources.my_classes_search_placeholder
import educationapp.shared.generated.resources.tab_feedback
import kotlinx.coroutines.flow.distinctUntilChanged
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
            null to stringResource(Res.string.lb_status_all),
            "ACTIVE" to stringResource(Res.string.lb_status_active),
            "COMPLETED" to stringResource(Res.string.lb_status_completed),
            "DROPPED" to stringResource(Res.string.lb_status_dropped)
        )

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

    @Composable
    private fun FeedbackListContent(
        state: FeedbackClassesState,
        childrenState: ParentChildrenState,
        lazyListState: LazyListState,
        maxScrollDp: Dp,
        totalHeaderHeightDp: Dp,
        listTopPaddingDp: Dp,
        onLoadNextPage: () -> Unit,
        onRetry: () -> Unit,
        onFeedbacksClick: (SchoolClass) -> Unit
    ) {
        val bottomBarHeight = LocalBottomBarHeight.current
        val parentMainScreenModel = LocalParentMainScreenModel.current

        LaunchedEffect(lazyListState) {
            snapshotFlow {
                val layoutInfo = lazyListState.layoutInfo
                val totalItemsNumber = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisibleItemIndex >= totalItemsNumber - 3
            }
                .distinctUntilChanged()
                .collect { shouldLoadMore ->
                    if (shouldLoadMore) {
                        onLoadNextPage()
                    }
                }
        }

        if (childrenState !is ParentChildrenState.Success) {
            when (childrenState) {
                is ParentChildrenState.Loading -> {
                    ListCardSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = totalHeaderHeightDp)
                            .padding(16.dp),
                        itemCount = 4
                    )
                    return
                }

                is ParentChildrenState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = totalHeaderHeightDp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateView(
                            error = childrenState.error,
                            onRetry = { parentMainScreenModel.loadChildren() }
                        )
                    }
                    return
                }
            }
        }

        when (state) {
            is FeedbackClassesState.Loading -> {
                ListCardSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = totalHeaderHeightDp)
                        .padding(16.dp),
                    itemCount = 4
                )
            }

            is FeedbackClassesState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = totalHeaderHeightDp)
                        .padding(AppDimen.p16),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ErrorStateView(
                        error = state.error,
                        onRetry = onRetry
                    )
                }
            }

            is FeedbackClassesState.Success -> {
                if (state.classes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = totalHeaderHeightDp)
                            .padding(AppDimen.p16),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AppDimen.p24),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = stringResource(Res.string.my_classes_empty),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = AppDimen.p16,
                            end = AppDimen.p16,
                            top = listTopPaddingDp,
                            bottom = AppDimen.p24 + bottomBarHeight
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(maxScrollDp))
                        }

                        items(state.classes, key = { it.id }) { schoolClass ->
                            ClassCard(
                                schoolClass = schoolClass,
                                onFeedbacksClick = { onFeedbacksClick(schoolClass) }
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
            }
        }
    }
}
