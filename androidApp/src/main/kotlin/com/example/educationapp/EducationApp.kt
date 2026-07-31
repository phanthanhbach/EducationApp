package com.example.educationapp

import android.app.Application
import com.example.educationapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class EducationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(baseUrl = BuildConfig.BASE_URL) {
            androidLogger()
            androidContext(this@EducationApp)
        }
    }
}
