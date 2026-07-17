package com.example.educationapp.domain.usecase

import com.example.educationapp.core.network.ApiResult
import com.example.educationapp.domain.repository.MockAssignmentRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class GradeSubmissionUseCaseTest {

    private val fakeRepository = MockAssignmentRepository()
    private val useCase = GradeSubmissionUseCase(fakeRepository)

    @Test
    fun testGradeSubmission_success() = runBlocking {
        // Given
        val classId = 1
        val studentId = 2
        val assignmentId = 3
        val score = 8.5
        val comment = "Good job!"
        fakeRepository.shouldReturnError = false

        // When
        val result = useCase(classId, studentId, assignmentId, score, comment)

        // Then
        assertTrue(result is ApiResult.Success)
        val submission = result.data
        assertEquals(classId, submission.classId)
        assertEquals(studentId, submission.studentId)
        assertEquals(assignmentId, submission.assignmentId)
        assertEquals(score, submission.score)
        assertEquals(comment, submission.teacherComment)

        // Verify repository interaction
        assertEquals(classId, fakeRepository.lastClassId)
        assertEquals(studentId, fakeRepository.lastStudentId)
        assertEquals(assignmentId, fakeRepository.lastAssignmentId)
        assertEquals(score, fakeRepository.lastScore)
        assertEquals(comment, fakeRepository.lastComment)
    }

    @Test
    fun testGradeSubmission_failure() = runBlocking {
        // Given
        val classId = 1
        val studentId = 2
        val assignmentId = 3
        val score = 5.0
        val comment = "Failed test"
        fakeRepository.shouldReturnError = true

        // When
        val result = useCase(classId, studentId, assignmentId, score, comment)

        // Then
        assertTrue(result is ApiResult.Error)
        assertTrue(result is ApiResult.Error.UnknownError)
        assertEquals("Fake repository error", result.message)
    }
}
