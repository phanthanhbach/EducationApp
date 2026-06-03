package com.example.educationapp.presentation.screen.main.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.educationapp.presentation.screen.profile.ProfileScreenContent
import com.example.educationapp.presentation.screen.setting.SettingScreen
import com.example.educationapp.presentation.screenmodel.profile.ProfileScreenModel
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_person_filled_24dp
import educationapp.shared.generated.resources.tab_profile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.tab_profile)
            val icon = painterResource(Res.drawable.ic_person_filled_24dp)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val profileState by screenModel.state.collectAsState()

        ProfileScreenContent(
            profileState = profileState,
            onSettingsClick = {
                navigator.parent?.push(SettingScreen())
            },
            onRetry = { screenModel.loadProfile() }
        )
    }
}
