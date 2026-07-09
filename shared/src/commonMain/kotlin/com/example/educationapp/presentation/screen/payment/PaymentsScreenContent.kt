package com.example.educationapp.presentation.screen.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.ClassStatusFilterBottomSheet
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.enums.StudentClassStatus
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.my_classes.ClassCard
import com.example.educationapp.presentation.screen.my_classes.resolveClassStatus
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import com.example.educationapp.presentation.screenmodel.payment.PaymentsTabState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.my_classes_empty
import educationapp.shared.generated.resources.my_classes_search_placeholder
import educationapp.shared.generated.resources.tab_payments
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreenContent(
    role: AppRole,
    state: PaymentsTabState,
    searchQuery: String,
    selectedStatus: String?,
    isRefreshing: Boolean,
    onSearch: (String) -> Unit,
    onStatusSelect: (String?) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onInvoiceClick: (SchoolClass) -> Unit,
    childrenState: ParentChildrenState? = null,
    selectedChild: UserProfile.Student? = null,
    onChildSelected: ((UserProfile.Student) -> Unit)? = null,
    onRefreshChildren: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showFilterSheet by remember { mutableStateOf(false) }

    val statuses = listOf(
        null to stringResource(Res.string.lb_status_all)
    ) + StudentClassStatus.entries.map { status ->
        status.name to stringResource(status.labelRes)
    }

    val isParent = role == AppRole.PARENT
    val bgColor =
        if (isParent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background

    val lazyListState = rememberLazyListState()

    SearchTopBarLayout(
        title = stringResource(Res.string.tab_payments),
        searchQuery = searchQuery,
        onSearch = onSearch,
        lazyListState = lazyListState,
        filterIcon = Res.drawable.ic_sort_24dp,
        isFilterActive = selectedStatus != null,
        placeholder = stringResource(Res.string.my_classes_search_placeholder),
        onFilterClick = { showFilterSheet = true },
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        extraContent = {
            if (isParent && childrenState != null && childrenState is ParentChildrenState.Success && onChildSelected != null) {
                ChildSelectorBar(
                    modifier = Modifier.padding(top = AppDimen.p12),
                    children = childrenState.children,
                    selectedChild = selectedChild,
                    onChildSelected = onChildSelected
                )
            }
        },
        modifier = modifier.background(bgColor)
    ) { maxScrollDp, totalHeaderHeightDp, listTopPaddingDp ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            PaymentsListContent(
                role = role,
                state = state,
                childrenState = childrenState,
                isParent = isParent,
                lazyListState = lazyListState,
                maxScrollDp = maxScrollDp,
                totalHeaderHeightDp = totalHeaderHeightDp,
                listTopPaddingDp = listTopPaddingDp,
                onLoadNextPage = onLoadNextPage,
                onRetry = onRetry,
                onRefreshChildren = onRefreshChildren,
                onInvoiceClick = onInvoiceClick
            )
        }
    }

    if (showFilterSheet) {
        ClassStatusFilterBottomSheet(
            selectedStatus = selectedStatus,
            statuses = statuses,
            onStatusSelect = onStatusSelect,
            onDismissRequest = { showFilterSheet = false }
        )
    }
}

@Composable
private fun PaymentsListContent(
    role: AppRole,
    state: PaymentsTabState,
    childrenState: ParentChildrenState?,
    isParent: Boolean,
    lazyListState: LazyListState,
    maxScrollDp: Dp,
    totalHeaderHeightDp: Dp,
    listTopPaddingDp: Dp,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    onRefreshChildren: (() -> Unit)?,
    onInvoiceClick: (SchoolClass) -> Unit
) {
    val bottomBarHeight = LocalBottomBarHeight.current

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

    // Parent-only: Children loading / error states
    if (isParent && childrenState != null && childrenState !is ParentChildrenState.Success) {
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
                        onRetry = { onRefreshChildren?.invoke() }
                    )
                }
                return
            }
        }
    }

    when (state) {
        is PaymentsTabState.Loading -> {
            ListCardSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = totalHeaderHeightDp)
                    .padding(16.dp),
                itemCount = 4
            )
        }

        is PaymentsTabState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = totalHeaderHeightDp)
                    .padding(AppDimen.p16),
                contentAlignment = Alignment.TopCenter
            ) {
                ErrorStateView(
                    error = state.message,
                    onRetry = onRetry
                )
            }
        }

        is PaymentsTabState.Success -> {
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
                        val (statusText, statusColor) = resolveClassStatus(schoolClass.status, role)
                        ClassCard(
                            schoolClass = schoolClass,
                            statusText = statusText,
                            statusColor = statusColor,
                            onInvoiceClick = { onInvoiceClick(schoolClass) }
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
