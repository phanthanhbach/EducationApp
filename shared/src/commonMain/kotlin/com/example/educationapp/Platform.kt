package com.example.educationapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform