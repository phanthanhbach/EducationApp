package com.example.educationapp

interface Platform {
    val name: String
    val appVersion: String
}

expect fun getPlatform(): Platform