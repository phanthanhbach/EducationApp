package com.example.educationapp.core.file

import androidx.compose.runtime.Composable

expect class UploadFilePicker {
    fun launch()
}

@Composable
expect fun rememberUploadFilePicker(
    onFileSelected: (UploadFile) -> Unit,
    onError: (String) -> Unit
): UploadFilePicker

