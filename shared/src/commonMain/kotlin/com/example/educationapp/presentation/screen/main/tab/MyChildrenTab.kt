package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.educationapp.core.ui.shimmer.skeleton.ListCardSkeleton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.core.theme.AppColor
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.layout.TopBarGreeting
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.domain.usecase.GetMyProfileUseCase
import com.example.educationapp.presentation.screen.main.LocalAppRole
import com.example.educationapp.presentation.screen.main.LocalParentMainScreenModel
import com.example.educationapp.presentation.screen.main.tab.component.ChildSelectorBar
import com.example.educationapp.presentation.screen.parent.ChildDetailCard
import com.example.educationapp.presentation.screenmodel.parent.ParentChildrenState
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_group_24dp
import educationapp.shared.generated.resources.tab_my_children
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

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

        val role = LocalAppRole.current
        val getMyProfileUseCase = koinInject<GetMyProfileUseCase>()
        var userName by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(role) {
            if (userName == null) {
                userName = when (val result = getMyProfileUseCase(role)) {
                    is ApiResult.Success -> {
                        result.data.fullName
                    }

                    else -> {
                        null
                    }
                }
            }
        }

        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(childrenState) {
            if (childrenState !is ParentChildrenState.Loading) {
                isRefreshing = false
            }
        }

        val onRefresh: () -> Unit = {
            isRefreshing = true
            parentMainScreenModel.loadChildren()
        }

        val scrollState = rememberScrollState()

        AppScaffold(
            topBar = {
                AppTopBar(
                    titleContent = {
                        TopBarGreeting(userName = userName)
                    },
                    scrollState = scrollState,
                    isTitleCentered = false
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
            ) {
                when (val state = childrenState) {
                    is ParentChildrenState.Loading -> {
                        ListCardSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            itemCount = 4
                        )
                    }

                    is ParentChildrenState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        if (childrenList.isNotEmpty()) {
                            ChildSelectorBar(
                                children = childrenList,
                                selectedChild = selectedChild,
                                onChildSelected = { parentMainScreenModel.selectChild(it) }
                            )

                            selectedChild?.let { child ->
                                ChildDetailCard(child = child)
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
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
