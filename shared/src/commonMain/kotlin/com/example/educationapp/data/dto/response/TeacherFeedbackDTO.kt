package com.example.educationapp.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TeacherFeedbackDTO(
    val id: Long,
    val feedbackType: String? = null,
    val studentId: Long,
    val studentName: String? = null,
    val teacherId: Long? = null,
    val teacherName: String? = null,
    val classId: Long,
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
