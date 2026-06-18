package com.example.educationapp.di

import com.example.educationapp.core.network.createHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient(get(), get()) }
}
