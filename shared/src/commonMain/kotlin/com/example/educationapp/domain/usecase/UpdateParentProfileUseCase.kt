package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.repository.ProfileRepository

class UpdateParentProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        parentId: Int,
        fullName: String,
        email: String,
        phoneNumber: String,
        address: String,
        img: String
    ): ApiResult<UserProfile.Parent> {
        return repository.updateParentProfile(
            parentId = parentId,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            address = address,
            img = img
        )
    }
}
