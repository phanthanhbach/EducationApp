package com.example.educationapp.presentation.screen.dashboard.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.educationapp.core.ui.text.AppText

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    AppText(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}