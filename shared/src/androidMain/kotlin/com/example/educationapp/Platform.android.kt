package com.example.educationapp

import android.content.Context
import android.os.Build
import org.koin.java.KoinJavaComponent

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val appVersion: String by lazy {
        try {
            val context: Context = KoinJavaComponent.get(Context::class.java)
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()