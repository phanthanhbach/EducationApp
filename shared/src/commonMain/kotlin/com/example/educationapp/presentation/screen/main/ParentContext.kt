package com.example.educationapp.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.presentation.screenmodel.parent.ParentMainScreenModel
import dev.chrisbanes.haze.HazeState

val LocalAppRole = compositionLocalOf<AppRole> {
    error("No AppRole provided. Ensure MainScreen provides LocalAppRole.")
}

val LocalParentMainScreenModel = compositionLocalOf<ParentMainScreenModel> {
    error("No ParentMainScreenModel provided")
}

val LocalSharedHazeState = staticCompositionLocalOf<HazeState?> { null }

val LocalBottomBarHeight = staticCompositionLocalOf<Dp> { 0.dp }
