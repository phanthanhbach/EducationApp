package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.SearchTextField
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import com.example.educationapp.presentation.screenmodel.parent.PaymentsScreenModel
import com.example.educationapp.presentation.screenmodel.parent.PaymentsTabState
import com.example.educationapp.presentation.screen.my_classes.ClassCard
import com.example.educationapp.presentation.screen.invoice.ClassInvoicesScreen
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_account_balance_wallet_24dp
import educationapp.shared.generated.resources.ic_filter_alt_24dp
import educationapp.shared.generated.resources.tab_payments
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.lb_status_dropped
import educationapp.shared.generated.resources.my_classes_empty
import educationapp.shared.generated.resources.profile_retry
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

            return remember(tabIndex) {
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
        var tempSelectedStatus by remember(selectedStatus) { mutableStateOf(selectedStatus) }

        val statuses = listOf(
            null to stringResource(Res.string.lb_status_all),
            "ACTIVE" to stringResource(Res.string.lb_status_active),
            "COMPLETED" to stringResource(Res.string.lb_status_completed),
            "DROPPED" to stringResource(Res.string.lb_status_dropped)
        )

        if (role == AppRole.PARENT) {
            val parentMainScreenModel = LocalParentMainScreenModel.current
            val childrenState by parentMainScreenModel.childrenState.collectAsState()
            val selectedChild by parentMainScreenModel.selectedChild.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AppTopBar(
                    titleContent = {
                        AppText(
                            text = stringResource(Res.string.tab_payments),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    isTitleCentered = false
                )

                when (val cState = childrenState) {
                    is ParentChildrenState.Loading -> {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColor.Primary)
                        }
                    }

                    is ParentChildrenState.Error -> {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = cState.message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }

                    is ParentChildrenState.Success -> {
                        val childrenList = cState.children
                        if (childrenList.isNotEmpty()) {
                            LaunchedEffect(selectedChild) {
                                screenModel.loadProfileAndClasses(role, selectedChild?.studentId?.toLong())
                            }

                            ChildSelectorBar(
                                children = childrenList,
                                selectedChild = selectedChild,
                                onChildSelected = { parentMainScreenModel.selectChild(it) }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppDimen.p16, vertical = AppDimen.p8),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SearchTextField(
                                    value = searchQuery,
                                    onSearch = { screenModel.searchClasses(it) },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (selectedStatus != null) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            }
                                        )
                                        .clickable {
                                            tempSelectedStatus = selectedStatus
                                            showFilterSheet = true
                                        }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(
                                        drawableRes = Res.drawable.ic_filter_alt_24dp,
                                        tint = if (selectedStatus != null) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        iconModifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                PaymentsListContent(
                                    state = state,
                                    onLoadNextPage = { screenModel.loadNextPage() },
                                    onRetry = { screenModel.loadProfileAndClasses(role, selectedChild?.studentId?.toLong()) },
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
                        } else {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = "Không có thông tin học sinh nào.",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LaunchedEffect(Unit) {
                screenModel.loadProfileAndClasses(role)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AppTopBar(
                    titleContent = {
                        AppText(
                            text = stringResource(Res.string.tab_payments),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    isTitleCentered = false
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimen.p16, vertical = AppDimen.p8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchTextField(
                        value = searchQuery,
                        onSearch = { screenModel.searchClasses(it) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedStatus != null) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                }
                            )
                            .clickable {
                                tempSelectedStatus = selectedStatus
                                showFilterSheet = true
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_filter_alt_24dp,
                            tint = if (selectedStatus != null) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            iconModifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    PaymentsListContent(
                        state = state,
                        onLoadNextPage = { screenModel.loadNextPage() },
                        onRetry = { screenModel.loadProfileAndClasses(role) },
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
            }
        }

        if (showFilterSheet) {
            AppBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimen.p24),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppText(
                        text = "Lọc trạng thái lớp học",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        statuses.forEach { (statusKey, statusLabel) ->
                            val isSelected = tempSelectedStatus == statusKey
                            AppChip(
                                text = statusLabel,
                                selected = isSelected,
                                onClick = { tempSelectedStatus = statusKey }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            screenModel.filterByStatus(tempSelectedStatus)
                            showFilterSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        AppText(
                            text = "Xác nhận",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PaymentsListContent(
        state: PaymentsTabState,
        onLoadNextPage: () -> Unit,
        onRetry: () -> Unit,
        onInvoiceClick: (SchoolClass) -> Unit
    ) {
        val lazyListState = rememberLazyListState()

        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
                    val lastVisibleItem = visibleItems.lastOrNull()
                    if (lastVisibleItem != null && lastVisibleItem.index >= lazyListState.layoutInfo.totalItemsCount - 3) {
                        onLoadNextPage()
                    }
                }
        }

        when (state) {
            is PaymentsTabState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColor.Primary)
                }
            }

            is PaymentsTabState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                            top = AppDimen.p12,
                            bottom = AppDimen.p24
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
