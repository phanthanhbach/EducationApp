package com.example.educationapp.core.util

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Decodes a ByteArray (e.g. JPEG/PNG bytes) into a Compose [ImageBitmap]
 * for preview purposes.
 */
expect fun decodeByteArrayToImageBitmap(bytes: ByteArray): ImageBitmap?
