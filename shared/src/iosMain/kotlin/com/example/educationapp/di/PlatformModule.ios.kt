package com.example.educationapp.di

import com.example.educationapp.core.data.createIosDataStore
import com.example.educationapp.core.data.createIosSecureSettings
import org.koin.dsl.module

actual val platformModule = module {
    single { createIosDataStore() }
    single { createIosSecureSettings() }
}
