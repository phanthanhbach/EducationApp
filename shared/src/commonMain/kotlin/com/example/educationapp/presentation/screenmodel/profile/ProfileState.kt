package com.example.educationapp.presentation.screenmodel.profile

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.UserProfile

sealed interface ProfileState {
    object Idle : ProfileState
    object Loading : ProfileState
    data class Success(val profile: UserProfile) : ProfileState
    data class Error(val error: UiText) : ProfileState
}