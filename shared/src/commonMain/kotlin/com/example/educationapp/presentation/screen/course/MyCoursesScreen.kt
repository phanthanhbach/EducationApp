package com.example.educationapp.presentation.screen.course

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.layout.SearchTopBarLayout
import com.example.educationapp.core.ui.sheet.AppBottomSheet
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.Course
import com.example.educationapp.presentation.screenmodel.course.MyCoursesScreenModel
import com.example.educationapp.presentation.screenmodel.course.MyCoursesState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.course_status_inactive
import educationapp.shared.generated.resources.dashboard_course_code
import educationapp.shared.generated.resources.dashboard_course_total_sessions
import educationapp.shared.generated.resources.dashboard_courses_empty
import educationapp.shared.generated.resources.dashboard_courses_title
import educationapp.shared.generated.resources.ic_sort_24dp
import educationapp.shared.generated.resources.lb_status_active
import educationapp.shared.generated.resources.lb_status_all
import educationapp.shared.generated.resources.profile_retry
import educationapp.shared.generated.resources.search_placeholder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

class MyCoursesScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MyCoursesScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val selectedStatus by screenModel.selectedStatus.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current
        var showFilterSheet by remember { mutableStateOf(false) }
        var tempSelectedStatus by remember(selectedStatus) { mutableStateOf(selectedStatus) }

        val lazyListState = rememberLazyListState()

        LaunchedEffect(Unit) {
            screenModel.loadCourses(append = false)
        }

        val statuses = listOf(
            null to stringResource(Res.string.lb_status_all),
            true to stringResource(Res.string.lb_status_active),
            false to stringResource(Res.string.course_status_inactive)
        )

        SearchTopBarLayout(
            title = stringResource(Res.string.dashboard_courses_title),
            searchQuery = searchQuery,
            onSearch = { screenModel.searchCourses(it) },
            lazyListState = lazyListState,
            placeholder = stringResource(Res.string.search_placeholder),
            filterIcon = Res.drawable.ic_sort_24dp,
            isFilterActive = selectedStatus != null,
            onFilterClick = {
                tempSelectedStatus = selectedStatus
                showFilterSheet = true
            },
            onBackClick = { navigator.pop() }
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
                when (val currentState = state) {
                    is MyCoursesState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = totalHeaderHeightDp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColor.Primary)
                        }
                    }

                    is MyCoursesState.Error -> {
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
                                        text = currentState.message,
                                        fontSize = 14.sp,
                                        color = AppColor.Error,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = { screenModel.loadCourses(append = false) },
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

                    is MyCoursesState.Success -> {
                        if (currentState.courses.isEmpty()) {
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
                                            text = stringResource(Res.string.dashboard_courses_empty),
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
                                            screenModel.loadCourses(append = true)
                                        }
                                    }
                            }

                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = AppDimen.p16,
                                    end = AppDimen.p16,
                                    top = listTopPaddingDp,
                                    bottom = AppDimen.p24
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(maxScrollDp))
                                }

                                itemsIndexed(currentState.courses) { index, course ->
                                    CourseItemCard(course = course)
                                }

                                if (currentState.hasNextPage) {
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
                        text = "Lọc trạng thái khóa học",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
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
    private fun CourseItemCard(
        course: Course,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(AppDimen.p16)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    AppText(
                        text = course.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        AppText(
                            text = course.level,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppDimen.p12))
                AppText(
                    text = stringResource(Res.string.dashboard_course_code, course.code),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(AppDimen.p4))
                AppText(
                    text = stringResource(Res.string.dashboard_course_total_sessions, course.totalSessions),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
