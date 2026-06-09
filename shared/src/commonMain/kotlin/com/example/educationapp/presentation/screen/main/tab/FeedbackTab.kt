package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_chat_24dp
import educationapp.shared.generated.resources.tab_feedback
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class FeedbackTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_feedback)
            val icon = painterResource(Res.drawable.ic_chat_24dp)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
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
                        text = stringResource(Res.string.tab_feedback),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                isTitleCentered = false
            )

            when (val state = childrenState) {
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
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
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
                                children = childrenList,
                                selectedChild = selectedChild,
                                onChildSelected = { parentMainScreenModel.selectChild(it) }
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            alpha = 0.4f
                                        )
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AppIcon(
                                            drawableRes = Res.drawable.ic_chat_24dp,
                                            tint = MaterialTheme.colorScheme.primary,
                                            iconModifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        AppText(
                                            text = "Nhận xét của giáo viên",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        AppText(
                                            text = "Tính năng xem nhận xét của giáo viên dành cho học sinh ${selectedChild?.fullName ?: ""} đang được phát triển và sẽ sớm ra mắt.",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            ),
                                            textAlign = TextAlign.Center
                                        )
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
