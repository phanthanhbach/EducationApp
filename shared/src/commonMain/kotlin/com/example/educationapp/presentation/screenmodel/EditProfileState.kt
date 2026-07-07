package com.example.educationapp.presentation.screenmodel

import androidx.compose.ui.graphics.ImageBitmap
import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.UserProfile
import kotlinx.datetime.LocalDate

sealed interface EditProfileUiState {
    object Idle : EditProfileUiState
    object Loading : EditProfileUiState
    data class StudentLoadSuccess(
        val student: UserProfile.Student,
        val fullName: String,
        val dateOfBirth: LocalDate?,
        val gender: String,
        val address: String,
        val zaloLink: String,
        val avatarPreview: ImageBitmap? = null
    ) : EditProfileUiState

    data class TeacherLoadSuccess(
        val teacher: UserProfile.Teacher,
        val fullName: String,
        val email: String,
        val phone: String,
        val certificates: List<String>,
        val experience: String,
        val avatarPreview: ImageBitmap? = null
    ) : EditProfileUiState

    data class ParentLoadSuccess(
        val parent: UserProfile.Parent,
        val fullName: String,
        val phone: String,
        val email: String,
        val address: String,
        val avatarPreview: ImageBitmap? = null
    ) : EditProfileUiState

    data class Error(val error: UiText) : EditProfileUiState
}

sealed interface SaveStatus {
    object Idle : SaveStatus
    object Saving : SaveStatus
    object Saved : SaveStatus
    data class Error(val error: UiText) : SaveStatus
}
