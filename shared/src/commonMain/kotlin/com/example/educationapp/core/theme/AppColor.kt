package com.example.educationapp.core.theme

import androidx.compose.ui.graphics.Color

object AppColor {
    // Brand Colors
    val Primary = Color(0xFF475AD7)
    val Secondary = Color(0xFF8E97FD)
    val Tertiary = Color(0xFFFEC33D)
    
    // Role Based Accents (Optional suggestions)
    val StudentAccent = Color(0xFF4CAF50) // Green - Growth
    val TeacherAccent = Color(0xFF2196F3) // Blue - Professional
    val ParentAccent = Color(0xFF00BCD4)  // Cyan - Trust
    
    // Functional Colors
    val Error = Color(0xFFFB4343)
    val Success = Color(0xFF2ED33C)
    val Warning = Color(0xFFFFBB00)
    
    // Neutral Colors (Light Mode)
    val BackgroundLight = Color(0xFFF3F4F6)
    val SurfaceLight = Color(0xFFFFFFFF)
    val TextPrimaryLight = Color(0xFF333647)
    val TextSecondaryLight = Color(0xFF7C82A1)
    
    // Neutral Colors (Dark Mode)
    val BackgroundDark = Color(0xFF1B1B1F)
    val SurfaceDark = Color(0xFF252529)
    val TextPrimaryDark = Color(0xFFF3F4F6)
    val TextSecondaryDark = Color(0xFFACAFC3)
}
