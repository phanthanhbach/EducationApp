package com.example.educationapp.presentation.screenmodel.parent

import com.example.educationapp.core.util.UiText
import com.example.educationapp.domain.entity.SchoolClass

sealed interface ChildClassesState {
    object Loading : ChildClassesState
    data class Success(val classes: List<SchoolClass>) : ChildClassesState
    data class Error(val message: UiText) : ChildClassesState
}
