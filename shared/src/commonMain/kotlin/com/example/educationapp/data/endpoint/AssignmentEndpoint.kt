package com.example.educationapp.data.endpoint

object AssignmentEndpoint {
    const val MY_REMINDERS = "assignments/me/reminders"
    const val FILTER = "assignments/filter"
    const val MY_FILTER = "assignments/me/filter"

    fun submit(assignmentId: Int): String = "assignments/$assignmentId/submit"
}
