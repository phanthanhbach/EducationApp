package com.example.educationapp.core.network

object CloudinaryConfig {
    const val CLOUD_NAME = "dzackryuv"
    const val CLOUDINARY_API_URL = "https://api.cloudinary.com/v1_1"
    const val CLOUDINARY_UPLOAD_URL = "$CLOUDINARY_API_URL/$CLOUD_NAME/image/upload"
    const val CLOUDINARY_UPLOAD_PRESET = "avatar"
    const val MAX_AVATAR_SIZE_BYTES = 5 * 1024 * 1024L // 5MB
}
