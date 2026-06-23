package com.example.educationapp.presentation.screen.my_classes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.sheet.ClassStatusFilterBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.enums.ClassStatus
import com.example.educationapp.presentation.screenmodel.assignment.AssignmentTabState
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_dropped
import educationapp.shared.generated.resources.lb_status_completed
import educationapp.shared.generated.resources.my_classes_empty
import educationapp.shared.generated.resources.my_classes_no_homework_desc
import educationapp.shared.generated.resources.my_classes_other_assignment_title
import educationapp.shared.generated.resources.my_classes_parent_assignment_title
import educationapp.shared.generated.resources.my_classes_student_assignment_title
import educationapp.shared.generated.resources.my_classes_search_placeholder
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.tab_classes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MyClassScreenContent(
    role: AppRole,
    state: AssignmentTabState,
    searchQuery: String,
    selectedStatus: String?,
    onSearch: (String) -> Unit,
    onStatusSelect: (String?) -> Unit,
    onAssignmentsClick: (Long, String) -> Unit,
    onFeedbacksClick: (Long, String) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (role == AppRole.TEACHER || role == AppRole.STUDENT) {
        ClassesContent(
            role = role,
            state = state,
            searchQuery = searchQuery,
            selectedStatus = selectedStatus,
            onSearch = onSearch,
            onStatusSelect = onStatusSelect,
            onAssignmentsClick = onAssignmentsClick,
            onFeedbacksClick = onFeedbacksClick,
            onLoadNextPage = onLoadNextPage,
            onRetry = onRetry,
            modifier = modifier
        )
    } else {
        OtherRolesClassesContent(
            role = role,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ClassesContent(
    role: AppRole,
    state: AssignmentTabState,
    searchQuery: String,
    selectedStatus: String?,
    onSearch: (String) -> Unit,
    onStatusSelect: (String?) -> Unit,
    onAssignmentsClick: (Long, String) -> Unit,
    onFeedbacksClick: (Long, String) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2500.milliseconds)
            toastMessage = null
        }
    }

    val statuses = if (role == AppRole.TEACHER) {
        listOf(
            null to stringResource(Res.string.lb_status_all)
        ) + ClassStatus.entries.map { status ->
            status.name to stringResource(status.labelRes)
        }
    } else {
        listOf(
            null to stringResource(Res.string.lb_status_all),
            "ACTIVE" to stringResource(Res.string.lb_status_active),
            "COMPLETED" to stringResource(Res.string.lb_status_completed),
            "DROPPED" to stringResource(Res.string.lb_status_dropped)
        )
    }

    val lazyListState = rememberLazyListState()

    SearchTopBarLayout(
        title = stringResource(Res.string.tab_classes),
        searchQuery = searchQuery,
        onSearch = onSearch,
        lazyListState = lazyListState,
        placeholder = stringResource(Res.string.my_classes_search_placeholder),
        filterIcon = Res.drawable.ic_sort_24dp,
        isFilterActive = selectedStatus != null,
        onFilterClick = {
            showFilterSheet = true
        },
        modifier = modifier
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
            when (state) {
                is AssignmentTabState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = totalHeaderHeightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColor.Primary)
                    }
                }

                is AssignmentTabState.Error -> {
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
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                    alpha = 0.1f
                                )
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

                is AssignmentTabState.Success -> {
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
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.3f
                                    )
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

                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = AppDimen.p16,
                                end = AppDimen.p16,
                                top = listTopPaddingDp,
                                bottom = AppDimen.p24 + LocalBottomBarHeight.current
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(maxScrollDp))
                            }

                            itemsIndexed(state.classes) { index, schoolClass ->
                                ClassCard(
                                    schoolClass = schoolClass,
                                    onAssignmentsClick = {
                                        onAssignmentsClick(schoolClass.id, schoolClass.name)
                                    },
                                    onFeedbacksClick = {
                                        onFeedbacksClick(schoolClass.id, schoolClass.name)
                                    }
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

            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
                    .padding(bottom = AppDimen.p24, start = AppDimen.p24, end = AppDimen.p24)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = AppDimen.p16,
                            vertical = AppDimen.p12
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppText(
                            text = toastMessage ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
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
private fun OtherRolesClassesContent(
    role: AppRole,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .padding(AppDimen.p16)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
        ) {
            AppText(
                text = stringResource(Res.string.tab_classes),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            val displayRoleText = when (role) {
                AppRole.STUDENT -> stringResource(Res.string.my_classes_student_assignment_title)
                AppRole.PARENT -> stringResource(Res.string.my_classes_parent_assignment_title)
                else -> stringResource(Res.string.my_classes_other_assignment_title)
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(AppDimen.p16)) {
                    AppText(
                        text = displayRoleText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppText(
                        text = stringResource(Res.string.my_classes_no_homework_desc),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
