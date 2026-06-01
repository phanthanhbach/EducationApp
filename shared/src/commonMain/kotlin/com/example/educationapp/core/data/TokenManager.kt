package com.example.educationapp.core.data

import com.example.educationapp.domain.enums.AppRole
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TokenManager(private val secureSettings: Settings) {

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_ROLE_KEY = "user_role"
        private const val USER_NAME_KEY = "user_name"
    }

    /** Lấy Access Token (Cơ chế đồng bộ của Settings) */
    fun getAccessToken(): String? = secureSettings.getStringOrNull(ACCESS_TOKEN_KEY)

    /** Lấy User Role đã lưu */
    fun getUserRole(): AppRole {
        val roleStr = secureSettings.getStringOrNull(USER_ROLE_KEY) ?: return AppRole.UNKNOWN
        return AppRole.fromString(roleStr)
    }

    /** Lấy Full Name đã lưu */
    fun getUserName(): String? = secureSettings.getStringOrNull(USER_NAME_KEY)

    /** Lưu session của người dùng (Token + Role + Name) */
    fun saveTokens(accessToken: String, refreshToken: String, role: String, fullName: String) {
        secureSettings[ACCESS_TOKEN_KEY] = accessToken
        secureSettings[REFRESH_TOKEN_KEY] = refreshToken
        secureSettings[USER_ROLE_KEY] = role
        secureSettings[USER_NAME_KEY] = fullName
    }

    /** Xóa sạch Token (khi Logout) */
    fun clearTokens() {
        secureSettings.remove(ACCESS_TOKEN_KEY)
        secureSettings.remove(REFRESH_TOKEN_KEY)
        secureSettings.remove(USER_ROLE_KEY)
        secureSettings.remove(USER_NAME_KEY)
    }
}
