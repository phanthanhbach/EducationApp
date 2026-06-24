package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.UserProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StudentDTOMappingTest {

    @Test
    fun testStudentDTO_toDomainEntity_mapsAllFieldsCorrectly() {
        // Given
        val dto = StudentDTO(
            studentId = 1,
            studentCode = "STU123",
            fullName = "John Doe",
            dateOfBirth = "2000-01-01",
            gender = "MALE",
            address = "123 Street",
            img = "avatar.png",
            zaloLink = "zalo.me/johndoe",
            currentLevel = "Intermediate",
            status = "ACTIVE",
            parentId = 99,
            parentName = "Parent Doe",
            createdAt = "2026-01-01",
            updatedAt = "2026-06-24",
            email = "johndoe@example.com",
            phoneNumber = "0987654321"
        )

        // When
        val entity = dto.toDomainEntity()

        // Then
        assertEquals(dto.studentId, entity.studentId)
        assertEquals(dto.studentCode, entity.studentCode)
        assertEquals(dto.fullName, entity.fullName)
        assertEquals(dto.dateOfBirth, entity.dateOfBirth)
        assertEquals(dto.gender, entity.gender)
        assertEquals(dto.address, entity.address)
        assertEquals(dto.img, entity.img)
        assertEquals(dto.zaloLink, entity.zaloLink)
        assertEquals(dto.currentLevel, entity.currentLevel)
        assertEquals(dto.status, entity.status)
        assertEquals(dto.parentId, entity.parentId)
        assertEquals(dto.parentName, entity.parentName)
        assertEquals(dto.createdAt, entity.createdAt)
        assertEquals(dto.updatedAt, entity.updatedAt)
        assertEquals(dto.email, entity.email)
        assertEquals(dto.phoneNumber, entity.phoneNumber)
    }

    @Test
    fun testStudentDTO_toDomainEntity_defaultsToNull() {
        // Given
        val dto = StudentDTO(
            studentId = 2,
            fullName = "Jane Doe"
        )

        // When
        val entity = dto.toDomainEntity()

        // Then
        assertEquals(2, entity.studentId)
        assertEquals("Jane Doe", entity.fullName)
        assertNull(entity.studentCode)
        assertNull(entity.email)
        assertNull(entity.phoneNumber)
        assertNull(entity.currentLevel)
    }
}
