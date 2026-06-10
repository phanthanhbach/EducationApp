package com.example.educationapp.data.dto.response

import com.example.educationapp.domain.entity.Feedback
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDTO(
    val id: Long? = null,
    val feedbackType: String? = null,
    val studentId: Long? = null,
    val studentName: String? = null,
    val teacherId: Long? = null,
    val teacherName: String? = null,
    val classId: Long? = null,
    val className: String? = null,
    val classStudentStatus: String? = null,
    val feedbackRating: Int? = null,
    val feedbackComment: String? = null,
    val feedbackAt: String? = null,
    val teacherFeedback: String? = null,
    val teacherFeedbackDate: String? = null,
    val status: String? = null,
    val createdAt: String? = null
)

fun FeedbackDTO.toDomainEntity() = Feedback(
    id = id ?: 0L,
    feedbackType = feedbackType ?: "",
    studentId = studentId ?: 0L,
    studentName = studentName ?: "",
    teacherId = teacherId ?: 0L,
    teacherName = teacherName ?: "",
    classId = classId ?: 0L,
    className = className ?: "",
    feedbackRating = feedbackRating ?: 0,
    feedbackComment = feedbackComment,
    feedbackAt = feedbackAt,
    teacherFeedback = teacherFeedback,
    teacherFeedbackDate = teacherFeedbackDate
)
