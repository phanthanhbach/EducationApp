package com.example.educationapp.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel

val LocalParentMainScreenModel = compositionLocalOf<ParentMainScreenModel> {
    error("No ParentMainScreenModel provided")
}
