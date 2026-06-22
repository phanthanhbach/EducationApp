package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.sheet.ClassStatusFilterBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.invoice.ClassInvoicesScreen
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.my_classes.ClassCard
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import com.example.educationapp.presentation.screenmodel.parent.PaymentsScreenModel
import com.example.educationapp.presentation.screenmodel.parent.PaymentsTabState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_account_balance_wallet_24dp
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.lb_status_dropped
import educationapp.shared.generated.resources.my_classes_empty
import educationapp.shared.generated.resources.my_classes_search_placeholder
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.tab_payments
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class PaymentsTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val role = LocalAppRole.current
            val tabIndex: UShort = if (role == AppRole.PARENT) 2u else 3u
            val title = stringResource(Res.string.tab_payments)
            val icon = painterResource(Res.drawable.ic_account_balance_wallet_24dp)

            return remember(title, icon, tabIndex) {
                TabOptions(
                    index = tabIndex,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val role = LocalAppRole.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<PaymentsScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()

        var showFilterSheet by remember { mutableStateOf(false) }

        val statuses = listOf(
            null to stringResource(Res.string.lb_status_all),
            "ACTIVE" to stringResource(Res.string.lb_status_active),
            "COMPLETED" to stringResource(Res.string.lb_status_completed),
            "DROPPED" to stringResource(Res.string.lb_status_dropped)
        )

        val isParent = role == AppRole.PARENT
        val bgColor =
            if (isParent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background

        // Parent-specific state
        val parentMainScreenModel = if (isParent) LocalParentMainScreenModel.current else null
        val childrenState = parentMainScreenModel?.childrenState?.collectAsState()?.value
        val selectedChild = parentMainScreenModel?.selectedChild?.collectAsState()?.value

        // Data loading
        if (isParent) {
            LaunchedEffect(selectedChild) {
                screenModel.loadProfileAndClasses(role, selectedChild?.studentId?.toLong())
            }
        } else {
            LaunchedEffect(Unit) {
                screenModel.loadProfileAndClasses(role)
            }
        }

        // Retry callback
        val onRetry: () -> Unit = if (isParent) {
            { screenModel.loadProfileAndClasses(role, selectedChild?.studentId?.toLong()) }
        } else {
            { screenModel.loadProfileAndClasses(role) }
        }

        val lazyListState = rememberLazyListState()

        SearchTopBarLayout(
            title = stringResource(Res.string.tab_payments),
            searchQuery = searchQuery,
            onSearch = { screenModel.searchClasses(it) },
            lazyListState = lazyListState,
            filterIcon = Res.drawable.ic_sort_24dp,
            isFilterActive = selectedStatus != null,
            placeholder = stringResource(Res.string.my_classes_search_placeholder),
            onFilterClick = {
                showFilterSheet = true
            },
            extraContent = {
                if (isParent && childrenState != null && childrenState is ParentChildrenState.Success) {
                    ChildSelectorBar(
                        children = childrenState.children,
                        selectedChild = selectedChild,
                        onChildSelected = { parentMainScreenModel.selectChild(it) }
                    )
                }
            },
            modifier = Modifier.background(bgColor)
        ) { maxScrollDp, totalHeaderHeightDp, listTopPaddingDp ->
            PaymentsListContent(
                state = state,
                childrenState = childrenState,
                isParent = isParent,
                lazyListState = lazyListState,
                maxScrollDp = maxScrollDp,
                totalHeaderHeightDp = totalHeaderHeightDp,
                listTopPaddingDp = listTopPaddingDp,
                onLoadNextPage = { screenModel.loadNextPage() },
                onRetry = onRetry,
                onInvoiceClick = { schoolClass ->
                    val studentId = (state as? PaymentsTabState.Success)?.studentId ?: 0L
                    navigator.parent?.push(
                        ClassInvoicesScreen(
                            classId = schoolClass.id.toInt(),
                            studentId = studentId.toInt(),
                            className = schoolClass.name
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
    private fun PaymentsListContent(
        state: PaymentsTabState,
        childrenState: ParentChildrenState?,
        isParent: Boolean,
        lazyListState: LazyListState,
        maxScrollDp: Dp,
        totalHeaderHeightDp: Dp,
        listTopPaddingDp: Dp,
        onLoadNextPage: () -> Unit,
        onRetry: () -> Unit,
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = totalHeaderHeightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColor.Primary)
                    }
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
                        AppText(
                            text = childrenState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                    return
                }
            }
        }

        when (state) {
            is PaymentsTabState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = totalHeaderHeightDp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColor.Primary)
                }
            }

            is PaymentsTabState.Error -> {
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
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, AppColor.Error.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(AppDimen.p16),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppText(
                                text = state.message,
                                fontSize = 14.sp,
                                color = AppColor.Error,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = AppColor.Primary)
                            ) {
                                AppText(
                                    text = stringResource(Res.string.profile_retry),
                                    color = Color.White
                                )
                            }
                        }
                    }
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
                        modifier = Modifier
                            .fillMaxSize(),
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
}
