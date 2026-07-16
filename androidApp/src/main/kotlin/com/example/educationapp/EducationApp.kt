package com.example.educationapp

import android.app.Application
import com.example.educationapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class EducationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val baseUrl = if (BuildConfig.DEBUG) {
            "http://10.68.125.218:8085/api/v1/"
        } else {
            "http://cnxvn.ddns.net:9000/api/v1/"
        }
        initKoin(baseUrl = baseUrl) {
            androidLogger()
            androidContext(this@EducationApp)
        }
    }
}
