package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.error.ErrorStateView
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.rating.AppRatingBar
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.Feedback
import com.example.educationapp.presentation.screen.main.LocalBottomBarHeight
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.parent.component.ClassChipsRow
import com.example.educationapp.presentation.screenmodel.parent.FeedbackClassesState
import com.example.educationapp.presentation.screenmodel.parent.FeedbackDetailState
import com.example.educationapp.presentation.screenmodel.parent.FeedbackScreenModel
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import dev.chrisbanes.haze.hazeSource
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_chat_24dp
import educationapp.shared.generated.resources.ic_star_24dp
import educationapp.shared.generated.resources.ic_star_filled_24dp
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
        val parentMainScreenModel = LocalParentMainScreenModel.current
        val childrenState by parentMainScreenModel.childrenState.collectAsState()
        val selectedChild by parentMainScreenModel.selectedChild.collectAsState()

        val screenModel = koinScreenModel<FeedbackScreenModel>()
        val classesState by screenModel.classesState.collectAsState()
        val selectedClass by screenModel.selectedClass.collectAsState()
        val feedbackState by screenModel.feedbackState.collectAsState()

        val scrollState = rememberScrollState()
        val sharedHazeState = LocalSharedHazeState.current
        val bottomBarHeight = LocalBottomBarHeight.current

        val isRefreshing by screenModel.isRefreshing.collectAsState()

        LaunchedEffect(selectedChild) {
            selectedChild?.let {
                screenModel.loadClasses(it.studentId.toLong())
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    titleContent = {
                        AppText(
                            text = stringResource(Res.string.tab_feedback),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    isTitleCentered = false
                )
            },
            isRefreshing = isRefreshing,
            onRefresh = { screenModel.refreshData() }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                when (val state = childrenState) {
                    is ParentChildrenState.Loading -> {
                        ListCardSkeleton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp),
                            itemCount = 4
                        )
                    }

                    is ParentChildrenState.Error -> {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorStateView(
                                error = UiText.DynamicString(state.message),
                                onRetry = { parentMainScreenModel.loadChildren() }
                            )
                        }
                    }

                    is ParentChildrenState.Success -> {
                        val childrenList = state.children
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (childrenList.isNotEmpty()) {
                                ChildSelectorBar(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(top = AppDimen.p12),
                                    children = childrenList,
                                    selectedChild = selectedChild,
                                    onChildSelected = { parentMainScreenModel.selectChild(it) }
                                )

                                when (val currentClassesState = classesState) {
                                    is FeedbackClassesState.Idle -> {
                                        // Do nothing
                                    }

                                    is FeedbackClassesState.Loading -> {
                                        ListCardSkeleton(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            itemCount = 3
                                        )
                                    }

                                    is FeedbackClassesState.Error -> {
                                        Box(
                                            modifier = Modifier.weight(1f).fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ErrorStateView(
                                                error = currentClassesState.error,
                                                onRetry = {
                                                    selectedChild?.let {
                                                        screenModel.loadClasses(it.studentId.toLong())
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    is FeedbackClassesState.Success -> {
                                        val classesList = currentClassesState.classes
                                        if (classesList.isEmpty()) {
                                            Box(
                                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AppText(
                                                    text = "Con chưa tham gia lớp học nào.",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        } else {
                                            Column(
                                                modifier = Modifier.weight(1f).fillMaxWidth()
                                            ) {
                                                ClassChipsRow(
                                                    classes = classesList,
                                                    selectedClass = selectedClass,
                                                    onClassSelected = { screenModel.selectClass(it) }
                                                )

                                                HorizontalDivider(
                                                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                        alpha = 0.4f
                                                    ),
                                                    thickness = 1.dp
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .let { modifier ->
                                                                if (sharedHazeState != null) modifier.hazeSource(
                                                                    state = sharedHazeState
                                                                ) else modifier
                                                            }
                                                            .verticalScroll(scrollState)
                                                            .padding(
                                                                start = 16.dp,
                                                                top = 12.dp,
                                                                end = 16.dp,
                                                                bottom = 12.dp + bottomBarHeight
                                                            ),
                                                        verticalArrangement = Arrangement.spacedBy(
                                                            16.dp
                                                        )
                                                    ) {
                                                        when (val currentFeedbackState =
                                                            feedbackState) {
                                                            is FeedbackDetailState.Idle -> {
                                                                // Do nothing
                                                            }

                                                            is FeedbackDetailState.Loading -> {
                                                                ListCardSkeleton(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    itemCount = 2
                                                                )
                                                            }

                                                            is FeedbackDetailState.Error -> {
                                                                ErrorStateView(
                                                                    modifier = Modifier.padding(16.dp),
                                                                    error = currentFeedbackState.error
                                                                )
                                                            }

                                                            is FeedbackDetailState.Success -> {
                                                                val feedback =
                                                                    currentFeedbackState.feedback
                                                                if (feedback != null) {
                                                                    // Card 1: Teacher Feedback Card
                                                                    TeacherFeedbackCard(feedback = feedback)

                                                                    // Card 2: Parent/Student Response Card
                                                                    ParentResponseCard(feedback = feedback)
                                                                } else {
                                                                    NoFeedbackPlaceholder()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
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
            }
        }
    }

    @Composable
    private fun RatingStars(
        rating: Int,
        modifier: Modifier = Modifier,
        maxStars: Int = 5,
        starSize: Dp = 18.dp
    ) {
        AppRatingBar(
            rating = rating,
            modifier = modifier,
            maxStars = maxStars,
            starSize = starSize,
            spacing = 3.dp,
            filledColor = Color(0xFFFFB300),
            unfilledColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
    }

    @Composable
    private fun TeacherFeedbackCard(feedback: Feedback) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = "Nhận xét của giáo viên",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    AppText(
                        text = "Giáo viên: ${feedback.teacherName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppText(
                        text = feedback.teacherFeedback ?: "Chưa có nội dung nhận xét chi tiết.",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    feedback.teacherFeedbackDate?.let { dateStr ->
                        val datePart = dateStr.substringBefore("T")
                        AppText(
                            text = "Ngày nhận xét: $datePart",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ParentResponseCard(feedback: Feedback) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = "Phản hồi của phụ huynh / học sinh",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    if (feedback.feedbackRating > 0) {
                        RatingStars(rating = feedback.feedbackRating)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                if (!feedback.feedbackComment.isNullOrBlank()) {
                    AppText(
                        text = feedback.feedbackComment,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    feedback.feedbackAt?.let { dateStr ->
                        val datePart = dateStr.substringBefore("T")
                        AppText(
                            text = "Ngày gửi phản hồi: $datePart",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                } else {
                    AppText(
                        text = "Chưa có ý kiến hay phản hồi từ phụ huynh cho nhận xét này.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    @Composable
    private fun NoFeedbackPlaceholder() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppIcon(
                        drawableRes = Res.drawable.ic_chat_24dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        iconModifier = Modifier.size(56.dp)
                    )
                    AppText(
                        text = "Chưa có nhận xét",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        text = "Giáo viên chưa gửi nhận xét học tập cho học sinh ở lớp học này.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
