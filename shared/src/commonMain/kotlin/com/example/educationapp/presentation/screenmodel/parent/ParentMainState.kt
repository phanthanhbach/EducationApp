package com.example.educationapp.presentation.screenmodel.parent

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.UserProfile

sealed interface ParentChildrenState {
    object Loading : ParentChildrenState
    data class Success(val children: List<UserProfile.Student>) : ParentChildrenState
    data class Error(val error: UiText) : ParentChildrenState
}
