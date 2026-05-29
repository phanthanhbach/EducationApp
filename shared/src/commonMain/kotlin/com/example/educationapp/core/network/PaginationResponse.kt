package com.example.educationapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse<T>(
    val items: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false
)
