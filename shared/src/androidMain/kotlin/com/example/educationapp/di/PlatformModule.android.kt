package com.example.educationapp.di

import com.example.educationapp.core.data.createAndroidDataStore
import com.example.educationapp.core.data.createAndroidSecureSettings
import org.koin.dsl.module

actual val platformModule = module {
    single { createAndroidDataStore(get()) }
    single { createAndroidSecureSettings(get()) }
}
