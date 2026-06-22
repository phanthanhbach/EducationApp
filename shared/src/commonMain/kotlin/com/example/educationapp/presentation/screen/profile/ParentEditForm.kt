package com.example.educationapp.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.file.rememberImagePickerCropper
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_email
import educationapp.shared.generated.resources.profile_full_name
import educationapp.shared.generated.resources.profile_phone
import org.jetbrains.compose.resources.stringResource

@Composable
fun ParentEditForm(
    state: EditProfileUiState.ParentLoadSuccess,
    saveStatus: SaveStatus,
    screenModel: EditProfileScreenModel
) {
    val imagePicker = rememberImagePickerCropper(
        onImageCropped = { bytes -> screenModel.onAvatarCropped(bytes) },
        onError = { /* Silently ignore or log */ }
    )

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
        // Avatar Section
        item {
            AvatarSection(
                imgUrl = state.parent.img,
                avatarPreview = state.avatarPreview,
                onEditClick = { imagePicker.launch() }
            )
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
                        value = state.email,
                        onValueChange = { screenModel.onEmailChanged(it) },
                        label = stringResource(Res.string.profile_email),
                        labelStyle = AppTextFieldLabelStyle.External,
                        modifier = Modifier.weight(1f)
                    )

                    AppTextField(
                        value = state.phone,
                        onValueChange = { screenModel.onPhoneChanged(it) },
                        label = stringResource(Res.string.profile_phone),
                        labelStyle = AppTextFieldLabelStyle.External,
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
            }
        }

        // Save Button
        item {
            SaveButtonSection(
                saveStatus = saveStatus,
                enabled = state.fullName.isNotBlank(),
                onSave = { screenModel.saveProfile() }
            )
        }
    }
}
