package com.example.educationapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse<T>(
    val content: List<T> = emptyList(),
    val number: Int = 0,
    val size: Int = 0,
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val last: Boolean = true,
    val first: Boolean = true
)
