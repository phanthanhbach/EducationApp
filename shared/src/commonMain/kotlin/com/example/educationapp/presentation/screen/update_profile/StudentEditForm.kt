package com.example.educationapp.presentation.screen.update_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.file.rememberImagePickerCropper
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle
import com.example.educationapp.core.ui.textfield.DatePickerTextField
import com.example.educationapp.core.ui.textfield.DropdownTextField
import com.example.educationapp.presentation.screenmodel.EditProfileScreenModel
import com.example.educationapp.presentation.screenmodel.EditProfileUiState
import com.example.educationapp.presentation.screenmodel.SaveStatus
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.gender_female
import educationapp.shared.generated.resources.gender_male
import educationapp.shared.generated.resources.profile_address
import educationapp.shared.generated.resources.profile_current_level
import educationapp.shared.generated.resources.profile_dob
import educationapp.shared.generated.resources.profile_full_name
import educationapp.shared.generated.resources.profile_gender
import educationapp.shared.generated.resources.profile_student_code
import educationapp.shared.generated.resources.profile_zalo
import educationapp.shared.generated.resources.profile_zalo_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun StudentEditForm(
    state: EditProfileUiState.StudentLoadSuccess,
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
                imgUrl = state.student.img,
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
