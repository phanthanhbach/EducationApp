package com.example.educationapp.core.ui.image

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource

sealed class CoreMediaSource {
    /** Tải ảnh từ Internet */
    data class Url(val url: String) : CoreMediaSource()
    
    /** Tải ảnh từ file path trong máy (ví dụ: file:///...) */
    data class LocalPath(val path: String) : CoreMediaSource()
    
    /** Sử dụng resource của Compose Multiplatform (thư mục composeResources) */
    data class ComposeResource(val resource: DrawableResource) : CoreMediaSource()
    
    /** Dùng trực tiếp ImageBitmap của Compose */
    data class Bitmap(val bitmap: ImageBitmap) : CoreMediaSource()
    
    /** Trường hợp không có ảnh */
    data object None : CoreMediaSource()
}
