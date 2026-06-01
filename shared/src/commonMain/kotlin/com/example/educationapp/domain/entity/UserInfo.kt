package com.example.educationapp.domain.entity

import com.example.educationapp.domain.enums.AppRole

data class UserInfo(
    val userRole: AppRole,
    val fullName: String
)
