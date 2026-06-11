package com.example.educationapp.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel

val LocalAppRole = compositionLocalOf<AppRole> {
    error("No AppRole provided. Ensure MainScreen provides LocalAppRole.")
}

val LocalParentMainScreenModel = compositionLocalOf<ParentMainScreenModel> {
    error("No ParentMainScreenModel provided")
}
