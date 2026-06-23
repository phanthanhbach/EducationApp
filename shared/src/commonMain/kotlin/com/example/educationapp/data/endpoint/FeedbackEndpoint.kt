package com.example.educationapp.data.endpoint

object FeedbackEndpoint {
    fun ratingSummary(teacherId: Long) = "feedbacks/teachers/$teacherId/rating-summary"
    fun teacherClassStudentFeedback(classId: Long, studentId: Long) =
        "feedbacks/teacher/classes/$classId/students/$studentId"
    fun studentClassFeedback(classId: Long) = "feedbacks/student/classes/$classId"
    const val FILTER = "feedbacks/filter"
}
