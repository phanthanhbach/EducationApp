package com.example.educationapp.data.endpoint

object AttendanceEndpoint {
    fun getScheduleAttendances(classId: Long, sessionNumber: Int): String {
        return "attendances/schedule/$classId/$sessionNumber"
    }
    const val SUBMIT = "attendances"
    const val RATE = "attendances/rate"
}
