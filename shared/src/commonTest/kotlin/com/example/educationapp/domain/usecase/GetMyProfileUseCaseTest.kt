package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.entity.UserProfile
import com.example.educationapp.domain.enums.AppRole
import com.example.educationapp.domain.repository.MockProfileRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMyProfileUseCaseTest {

    private val fakeRepository = MockProfileRepository()
    private val useCase = GetMyProfileUseCase(fakeRepository)

    @Test
    fun testGetMyProfile_student_success() = runBlocking {
        // Given
        val expectedStudent = UserProfile.Student(
            studentId = 1,
            studentCode = "STU1",
            fullName = "Alice",
            dateOfBirth = "2005-05-05",
            gender = "FEMALE",
            address = "456 Road",
            img = null,
            zaloLink = null,
            currentLevel = "Beginner",
            status = "ACTIVE",
            parentId = null,
            parentName = null,
            createdAt = null,
            updatedAt = null,
            email = "alice@gmail.com",
            phoneNumber = "0321000003"
        )
        fakeRepository.stubbedProfile = expectedStudent
        fakeRepository.shouldReturnError = false

        // When
        val result = useCase(AppRole.STUDENT)

        // Then
        assertTrue(result is ApiResult.Success)
        val profile = (result as ApiResult.Success).data
        assertTrue(profile is UserProfile.Student)
        assertEquals(expectedStudent.fullName, profile.fullName)
        assertEquals(expectedStudent.email, profile.email)
        assertEquals(expectedStudent.phoneNumber, profile.phoneNumber)
        assertEquals(AppRole.STUDENT, fakeRepository.lastRequestedRole)
    }

    @Test
    fun testGetMyProfile_failure() = runBlocking {
        // Given
        fakeRepository.shouldReturnError = true

        // When
        val result = useCase(AppRole.STUDENT)

        // Then
        assertTrue(result is ApiResult.Error)
        assertTrue(result is ApiResult.Error.UnknownError)
        assertEquals("Fake repository error", (result as ApiResult.Error.UnknownError).message)
    }
}

