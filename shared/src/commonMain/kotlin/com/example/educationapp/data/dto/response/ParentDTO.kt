package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ParentDTO(
    val parentId: Int,
    val fullName: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val active: Boolean? = null,
    val img: String? = null,
    val createdAt: String? = null
)
