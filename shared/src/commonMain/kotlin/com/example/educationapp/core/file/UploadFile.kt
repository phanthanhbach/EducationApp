package com.example.educationapp.core.file

data class UploadFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String? = null,
    val sizeBytes: Long = bytes.size.toLong()
)

