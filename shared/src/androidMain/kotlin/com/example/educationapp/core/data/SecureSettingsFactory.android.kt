package com.example.educationapp.core.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

/**
 * Tạo EncryptedSharedPreferences cho Android.
 * Mặc dù EncryptedSharedPreferences bị đánh dấu là lỗi thời (deprecated) bởi Google 
 * để khuyến khích dùng DataStore, nó vẫn là giải pháp ổn định nhất hiện nay để 
 * tích hợp Keystore vào hệ thống multiplatform-settings.
 */
@Suppress("DEPRECATION")
fun createAndroidSecureSettings(context: Context): Settings {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    return SharedPreferencesSettings(sharedPreferences)
}
