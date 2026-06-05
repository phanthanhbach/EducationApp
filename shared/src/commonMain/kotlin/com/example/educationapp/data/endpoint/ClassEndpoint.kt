package com.example.educationapp.data.endpoint

object ClassEndpoint {
    const val FILTER = "classes/filter"
    fun studentClasses(studentId: Long) = "student-classes/student/$studentId"
}

