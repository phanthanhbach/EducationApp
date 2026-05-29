package com.example.educationapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val timestamp: String,
    val message: String,
    val data: T
)
