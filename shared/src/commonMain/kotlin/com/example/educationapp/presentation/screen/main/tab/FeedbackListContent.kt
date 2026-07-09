package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.entity.SchoolClass
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.my_classes.ClassCard
import com.example.educationapp.presentation.screen.my_classes.resolveClassStatus
import com.example.educationapp.presentation.screenmodel.feedback.FeedbackClassesState
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.my_classes_empty
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeedbackListContent(
    state: FeedbackClassesState,
    childrenState: ParentChildrenState,
    lazyListState: LazyListState,
    maxScrollDp: Dp,
    totalHeaderHeightDp: Dp,
    listTopPaddingDp: Dp,
    onLoadNextPage: () -> Unit,
    onRetry: () -> Unit,
    onFeedbacksClick: (SchoolClass) -> Unit,
    modifier: Modifier = Modifier
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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = totalHeaderHeightDp)
                        .padding(16.dp),
                    itemCount = 4
                )
                return
            }

            is ParentChildrenState.Error -> {
                Box(
                    modifier = modifier
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
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = totalHeaderHeightDp)
                    .padding(16.dp),
                itemCount = 4
            )
        }

        is FeedbackClassesState.Error -> {
            Box(
                modifier = modifier
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
                    modifier = modifier
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
                    modifier = modifier.fillMaxSize(),
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
                        val (statusText, statusColor) = resolveClassStatus(schoolClass.status, AppRole.PARENT)
                        ClassCard(
                            schoolClass = schoolClass,
                            statusText = statusText,
                            statusColor = statusColor,
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
