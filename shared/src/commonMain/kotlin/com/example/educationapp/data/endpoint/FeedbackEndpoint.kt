package com.example.educationapp.data.endpoint

object FeedbackEndpoint {
    fun ratingSummary(teacherId: Long) = "feedbacks/teachers/$teacherId/rating-summary"
}
