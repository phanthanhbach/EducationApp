package com.example.educationapp.di

import com.example.educationapp.core.network.createHttpClient
import com.example.educationapp.core.data.SessionManager
import org.koin.dsl.module

val networkModule = module {
    single { SessionManager() }
    single { createHttpClient(get(), get(), get()) }
}
