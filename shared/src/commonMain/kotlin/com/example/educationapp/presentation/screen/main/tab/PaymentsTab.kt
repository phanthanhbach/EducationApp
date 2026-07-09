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
import com.example.educationapp.presentation.screen.invoice.ClassInvoicesScreen
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.payment.PaymentsScreenContent
import com.example.educationapp.presentation.screenmodel.payment.PaymentsScreenModel
import com.example.educationapp.presentation.screenmodel.payment.PaymentsTabState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_account_balance_wallet_24dp
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

    @Composable
    override fun Content() {
        val role = LocalAppRole.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<PaymentsScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        val isParent = role == AppRole.PARENT

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

        PaymentsScreenContent(
            role = role,
            state = state,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            isRefreshing = isRefreshing,
            onSearch = { screenModel.searchClasses(it) },
            onStatusSelect = { screenModel.filterByStatus(it) },
            onLoadNextPage = { screenModel.loadNextPage() },
            onRetry = onRetry,
            onRefresh = { screenModel.refreshData() },
            onInvoiceClick = { schoolClass ->
                val studentId = (state as? PaymentsTabState.Success)?.studentId ?: 0L
                navigator.parent?.push(
                    ClassInvoicesScreen(
                        classId = schoolClass.id.toInt(),
                        studentId = studentId.toInt(),
                        className = schoolClass.name
                    )
                )
            },
            childrenState = childrenState,
            selectedChild = selectedChild,
            onChildSelected = { parentMainScreenModel?.selectChild(it) },
            onRefreshChildren = { parentMainScreenModel?.loadChildren() }
        )
    }
}
