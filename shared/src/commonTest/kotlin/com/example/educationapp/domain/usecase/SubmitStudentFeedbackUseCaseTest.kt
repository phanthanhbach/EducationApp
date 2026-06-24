package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.MockClassFeedbackRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class SubmitStudentFeedbackUseCaseTest {

    private val fakeRepository = MockClassFeedbackRepository()
    private val useCase = SubmitStudentFeedbackUseCase(fakeRepository)

    @Test
    fun testSubmitStudentFeedback_success() = runBlocking {
        // Given
        val classId = 1L
        val rating = 5
        val comment = "Great class!"
        fakeRepository.shouldReturnError = false

        // When
        val result = useCase(classId, rating, comment)

        // Then
        assertTrue(result is ApiResult.Success)
        val feedback = result.data
        assertEquals(classId, feedback.classId)
        assertEquals(rating, feedback.feedbackRating)
        assertEquals(comment, feedback.feedbackComment)
        assertEquals(classId, fakeRepository.lastClassId)
        assertEquals(rating, fakeRepository.lastRating)
        assertEquals(comment, fakeRepository.lastComment)
    }

    @Test
    fun testSubmitStudentFeedback_failure() = runBlocking {
        // Given
        val classId = 1L
        val rating = 3
        val comment = "Okay class"
        fakeRepository.shouldReturnError = true

        // When
        val result = useCase(classId, rating, comment)

        // Then
        assertTrue(result is ApiResult.Error)
        assertTrue(result is ApiResult.Error.UnknownError)
        assertEquals("Fake repository error", result.message)
    }
}

