package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.LocalSharedHazeState
import dev.chrisbanes.haze.HazeState
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.parent.ChildDetailCard
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_group_24dp
import educationapp.shared.generated.resources.tab_my_children
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class MyChildrenTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_my_children)
            val icon = painterResource(Res.drawable.ic_group_24dp)

            return remember(title, icon) {
                TabOptions(
                    index = 0u,
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

        val scrollState = rememberScrollState()
        val sharedHazeState = LocalSharedHazeState.current

        AppScaffold(
            topBar = {
                AppTopBar(
                    titleContent = {
                        AppText(
                            text = stringResource(Res.string.tab_my_children),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    scrollState = scrollState
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(scrollState)
                                    ) {
                                        selectedChild?.let { child ->
                                            ChildDetailCard(child = child)
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
}
