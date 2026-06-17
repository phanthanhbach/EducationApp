package com.example.educationapp.data.endpoint

object TeacherCheckInEndpoint {
    const val CHECK_IN = "teacher-checkins/check-in"
    const val SESSION = "teacher-checkins/session"
    const val TEACHER_CHECK_IN = "teacher-checkins/teacher"
    fun checkOut(checkinId: Long) = "teacher-checkins/$checkinId/check-out"
}
