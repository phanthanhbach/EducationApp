package com.example.educationapp.domain.entity

/**
 * Sealed class representing a user profile.
 * Common fields are defined in the base class; role-specific fields live in each subclass.
 */
sealed class UserProfile(
    open val fullName: String,
    open val img: String?,
    open val status: String?
) {
    data class Teacher(
        val teacherId: Int,
        val teacherCode: String?,
        override val fullName: String,
        val email: String?,
        val phone: String?,
        override val img: String?,
        val certificates: List<String>,
        val hourlyRate: Double?,
        val experience: String?,
        override val status: String?,
        val createdAt: String?,
        val updatedAt: String?
    ) : UserProfile(fullName, img, status)

    data class Student(
        val studentId: Int,
        val studentCode: String?,
        override val fullName: String,
        val dateOfBirth: String?,
        val gender: String?,
        val address: String?,
        override val img: String?,
        val zaloLink: String?,
        val currentLevel: String?,
        override val status: String?,
        val parentId: Int?,
        val parentName: String?,
        val createdAt: String?,
        val updatedAt: String?
    ) : UserProfile(fullName, img, status)

    data class Parent(
        val parentId: Int,
        override val fullName: String,
        val phoneNumber: String?,
        val email: String?,
        val address: String?,
        val active: Boolean?,
        override val img: String?,
        val createdAt: String?
    ) : UserProfile(fullName, img, if (active == true) "ACTIVE" else "INACTIVE")
}
