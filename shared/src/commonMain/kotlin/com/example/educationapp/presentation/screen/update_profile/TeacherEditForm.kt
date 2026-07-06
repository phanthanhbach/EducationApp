package com.example.educationapp.presentation.screen.update_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.button.AppTextButton
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.file.rememberImagePickerCropper
import com.example.educationapp.presentation.screenmodel.EditProfileScreenModel
import com.example.educationapp.presentation.screenmodel.EditProfileUiState
import com.example.educationapp.presentation.screenmodel.SaveStatus
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_add_24dp
import educationapp.shared.generated.resources.ic_close_24dp
import educationapp.shared.generated.resources.ic_subdirectory_arrow_right_24dp
import educationapp.shared.generated.resources.profile_add_certificate
import educationapp.shared.generated.resources.profile_certificates
import educationapp.shared.generated.resources.profile_email
import educationapp.shared.generated.resources.profile_experience
import educationapp.shared.generated.resources.profile_full_name
import educationapp.shared.generated.resources.profile_phone
import educationapp.shared.generated.resources.profile_teacher_code
import org.jetbrains.compose.resources.stringResource

@Composable
fun TeacherEditForm(
    state: EditProfileUiState.TeacherLoadSuccess,
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
                imgUrl = state.teacher.img,
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

                AppTextField(
                    value = state.teacher.teacherCode ?: "",
                    onValueChange = {},
                    label = stringResource(Res.string.profile_teacher_code),
                    labelStyle = AppTextFieldLabelStyle.External,
                    enabled = false,
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
            }
        }

        // Certificates Section
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppText(
                    text = stringResource(Res.string.profile_certificates),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        itemsIndexed(
            items = state.certificates,
            key = { index, _ -> "cert_$index" }
        ) { index, certificate ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(
                    drawableRes = Res.drawable.ic_subdirectory_arrow_right_24dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    iconModifier = Modifier.size(20.dp),
                    boxModifier = Modifier.size(24.dp)
                )

                AppTextField(
                    value = certificate,
                    onValueChange = { screenModel.onCertificateChanged(index, it) },
                    placeholder = stringResource(Res.string.profile_certificates),
                    modifier = Modifier.weight(1f)
                )

                AppIcon(
                    drawableRes = Res.drawable.ic_close_24dp,
                    tint = MaterialTheme.colorScheme.error,
                    iconModifier = Modifier.size(20.dp),
                    boxModifier = Modifier.size(40.dp),
                    onClick = { screenModel.removeCertificate(index) }
                )
            }
        }

        // Add Certificate Button
        item {
            AppTextButton(
                text = stringResource(Res.string.profile_add_certificate),
                onClick = { screenModel.addCertificate() },
                leadingIcon = {
                    AppIcon(
                        drawableRes = Res.drawable.ic_add_24dp,
                        tint = MaterialTheme.colorScheme.primary,
                        iconModifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // Experience
        item {
            AppTextField(
                value = state.experience,
                onValueChange = { screenModel.onExperienceChanged(it) },
                label = stringResource(Res.string.profile_experience),
                labelStyle = AppTextFieldLabelStyle.External,
                singleLine = false,
                maxLines = 5,
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
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
