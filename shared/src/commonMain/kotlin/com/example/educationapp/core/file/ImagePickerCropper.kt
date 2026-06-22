package com.example.educationapp.core.file

import androidx.compose.runtime.Composable

/**
 * Platform-specific image picker with built-in crop (1:1 square).
 * Returns cropped image as JPEG ByteArray.
 */
expect class ImagePickerCropper {
    fun launch()
}

@Composable
expect fun rememberImagePickerCropper(
    onImageCropped: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerCropper
