package com.example.educationapp.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TokenManager(private val secureSettings: Settings) {

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    /** Lấy Access Token (Cơ chế đồng bộ của Settings) */
    fun getAccessToken(): String? = secureSettings.getStringOrNull(ACCESS_TOKEN_KEY)

    /** Lưu Token vào vùng nhớ bảo mật (Keychain/Keystore) */
    fun saveTokens(accessToken: String, refreshToken: String) {
        secureSettings[ACCESS_TOKEN_KEY] = accessToken
        secureSettings[REFRESH_TOKEN_KEY] = refreshToken
    }

    /** Xóa sạch Token (khi Logout) */
    fun clearTokens() {
        secureSettings.remove(ACCESS_TOKEN_KEY)
        secureSettings.remove(REFRESH_TOKEN_KEY)
    }
}
