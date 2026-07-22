package com.example.educationapp.presentation.screen.parent.child_attendance

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.theme.screenPadding
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.screen.parent.child_attendance.composable.ClassDetailSection
import com.example.educationapp.presentation.screen.parent.child_attendance.composable.SummaryCard
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ChildAttendanceRateState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.parent_attendance_empty_classes
import educationapp.shared.generated.resources.parent_attendance_rate_title_format
import educationapp.shared.generated.resources.search_placeholder
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class ChildAttendanceRateScreen(
    private val studentId: Long,
    private val studentName: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel =
            koinScreenModel<ChildAttendanceRateScreenModel> { parametersOf(studentId) }

        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val isRefreshing by screenModel.isRefreshing.collectAsState()

        val lazyListState = rememberLazyListState()
        val currentState = state

        val horizontalPadding = AppDimen.screenPadding

        SearchTopBarLayout(
            title = stringResource(Res.string.parent_attendance_rate_title_format, studentName),
            searchQuery = searchQuery,
            onSearch = { screenModel.searchClasses(it) },
            lazyListState = lazyListState,
            placeholder = stringResource(Res.string.search_placeholder),
            onBackClick = { navigator.pop() },
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() },
            extraContent = {
                (currentState as? ChildAttendanceRateState.Success)?.let { successState ->
                    SummaryCard(
                        total = successState.summaryTotal,
                        attended = successState.summaryAttended,
                        absent = successState.summaryAbsent,
                        rate = successState.summaryRate,
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = AppDimen.p8
                        )
                    )
                }
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) { maxScrollDp, totalHeaderHeightDp, listTopPaddingDp ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentState) {
                    is ChildAttendanceRateState.Loading -> {
                        ListCardSkeleton(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = totalHeaderHeightDp)
                                .padding(horizontalPadding),
                            itemCount = 4
                        )
                    }

                    is ChildAttendanceRateState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = totalHeaderHeightDp)
                                .padding(horizontalPadding),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            ErrorStateView(
                                error = currentState.message,
                                onRetry = { screenModel.loadData(append = false) }
                            )
                        }
                    }

                    is ChildAttendanceRateState.Success -> {
                        if (currentState.classes.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = totalHeaderHeightDp)
                                    .padding(horizontalPadding),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(AppDimen.p12),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            alpha = 0.3f
                                        )
                                    ),
                                    border = BorderStroke(
                                        AppDimen.p1,
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
                                            text = stringResource(Res.string.parent_attendance_empty_classes),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            LaunchedEffect(lazyListState) {
                                snapshotFlow {
                                    val layoutInfo = lazyListState.layoutInfo
                                    val totalItemsNumber = layoutInfo.totalItemsCount
                                    val lastVisibleItemIndex =
                                        layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                                    lastVisibleItemIndex >= totalItemsNumber - 3
                                }
                                    .distinctUntilChanged()
                                    .collect { shouldLoadMore ->
                                        if (shouldLoadMore) {
                                            screenModel.loadData(append = true)
                                        }
                                    }
                            }

                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = horizontalPadding,
                                    end = horizontalPadding,
                                    top = listTopPaddingDp,
                                    bottom = AppDimen.p24
                                ),
                                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(maxScrollDp))
                                }

                                items(currentState.classes) { schoolClass ->
                                    val classRate = currentState.rates[schoolClass.id]
                                    ClassDetailSection(
                                        schoolClass = schoolClass,
                                        rate = classRate
                                    )
                                }

                                if (currentState.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(AppDimen.p16),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(AppDimen.p24)
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
    }}