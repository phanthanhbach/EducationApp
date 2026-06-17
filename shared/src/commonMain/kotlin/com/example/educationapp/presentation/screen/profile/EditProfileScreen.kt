package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.card.ProfileErrorCard
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.image.AppImage
import com.example.educationapp.core.ui.image.CoreMediaSource
import com.example.educationapp.core.ui.layout.AppScaffold
import com.example.educationapp.core.ui.layout.AppTopBar
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.ui.textfield.DatePickerTextField
import com.example.educationapp.core.ui.textfield.DropdownTextField
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.btn_edit_profile
import educationapp.shared.generated.resources.btn_save_changes
import educationapp.shared.generated.resources.gender_female
import educationapp.shared.generated.resources.gender_male
import educationapp.shared.generated.resources.ic_edit_24dp
import educationapp.shared.generated.resources.ic_person_filled_24dp
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_change_avatar
import educationapp.shared.generated.resources.profile_current_level
import educationapp.shared.generated.resources.profile_dob
import educationapp.shared.generated.resources.profile_full_name
import educationapp.shared.generated.resources.profile_gender
import educationapp.shared.generated.resources.profile_student_code
import educationapp.shared.generated.resources.profile_zalo
import educationapp.shared.generated.resources.profile_zalo_placeholder
import org.jetbrains.compose.resources.stringResource

class EditProfileScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<EditProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val saveStatus by screenModel.saveStatus.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(saveStatus) {
            if (saveStatus is SaveStatus.Saved) {
                screenModel.resetSaveStatus()
                navigator.pop()
            }
        }

        AppScaffold(
            topBar = {
                AppTopBar(
                    title = stringResource(Res.string.btn_edit_profile),
                    onBackClick = { navigator.pop() },
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is EditProfileUiState.Loading, is EditProfileUiState.Idle -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }

                    is EditProfileUiState.Error -> {
                        ProfileErrorCard(
                            message = state.message,
                            onRetry = { screenModel.loadProfile() }
                        )
                    }

                    is EditProfileUiState.LoadSuccess -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = AppDimen.p16,
                                top = AppDimen.p16,
                                end = AppDimen.p16,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Avatar & Header Section
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    AppText(
                                        text = stringResource(Res.string.profile_change_avatar),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    val mediaSource = if (!state.student.img.isNullOrBlank()) {
                                        CoreMediaSource.Url(state.student.img)
                                    } else {
                                        CoreMediaSource.None
                                    }

                                    Box(
                                        modifier = Modifier.size(90.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(45.dp)
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.background,
                                                    shape = RoundedCornerShape(45.dp)
                                                )
                                                .clip(RoundedCornerShape(45.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (mediaSource is CoreMediaSource.None) {
                                                AppIcon(
                                                    drawableRes = Res.drawable.ic_person_filled_24dp,
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    iconModifier = Modifier.size(45.dp)
                                                )
                                            } else {
                                                AppImage(
                                                    source = mediaSource,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }
                                        }

                                        // Edit Badge Icon in the corner (BottomEnd)
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .size(28.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.background,
                                                    shape = RoundedCornerShape(14.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppIcon(
                                                drawableRes = Res.drawable.ic_edit_24dp,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                iconModifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Form Fields
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    AppTextField(
                                        value = state.fullName,
                                        onValueChange = { screenModel.onFullNameChanged(it) },
                                        label = stringResource(Res.string.profile_full_name),
                                        labelStyle = AppTextFieldLabelStyle.External,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        AppTextField(
                                            value = state.student.studentCode ?: "",
                                            onValueChange = {},
                                            label = stringResource(Res.string.profile_student_code),
                                            labelStyle = AppTextFieldLabelStyle.External,
                                            enabled = false,
                                            modifier = Modifier.weight(1f)
                                        )

                                        DatePickerTextField(
                                            label = stringResource(Res.string.profile_dob),
                                            selectedDate = state.dateOfBirth,
                                            onDateSelected = { screenModel.onDateOfBirthChanged(it) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        val maleLabel = stringResource(Res.string.gender_male)
                                        val femaleLabel = stringResource(Res.string.gender_female)
                                        val genderOptions = listOf("MALE", "FEMALE")

                                        DropdownTextField(
                                            label = stringResource(Res.string.profile_gender),
                                            selectedValue = state.gender,
                                            options = genderOptions,
                                            optionLabel = { option ->
                                                when (option) {
                                                    "MALE" -> maleLabel
                                                    "FEMALE" -> femaleLabel
                                                    else -> option
                                                }
                                            },
                                            onValueSelected = { screenModel.onGenderChanged(it) },
                                            modifier = Modifier.weight(1f)
                                        )

                                        AppTextField(
                                            value = state.student.currentLevel ?: "",
                                            onValueChange = {},
                                            label = stringResource(Res.string.profile_current_level),
                                            labelStyle = AppTextFieldLabelStyle.External,
                                            enabled = false,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    AppTextField(
                                        value = state.address,
                                        onValueChange = { screenModel.onAddressChanged(it) },
                                        label = stringResource(Res.string.profile_address),
                                        labelStyle = AppTextFieldLabelStyle.External,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    AppTextField(
                                        value = state.zaloLink,
                                        onValueChange = { screenModel.onZaloLinkChanged(it) },
                                        label = stringResource(Res.string.profile_zalo),
                                        labelStyle = AppTextFieldLabelStyle.External,
                                        placeholder = stringResource(Res.string.profile_zalo_placeholder),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Save Action Section
                            item {
                                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                    if (saveStatus is SaveStatus.Error) {
                                        Text(
                                            text = (saveStatus as SaveStatus.Error).message,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { screenModel.saveProfile() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = saveStatus !is SaveStatus.Saving && state.fullName.isNotBlank()
                                    ) {
                                        if (saveStatus is SaveStatus.Saving) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            AppText(
                                                text = stringResource(Res.string.btn_save_changes),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onPrimary
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
    }
}
