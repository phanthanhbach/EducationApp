package com.example.educationapp.core.util

/**
 * Lớp tiện ích quản lý toàn bộ logic Validation cho ứng dụng.
 * Giúp tái sử dụng các biểu thức Regex ở nhiều màn hình (Login, Register, Profile, ForgotPassword...)
 */
object Validator {
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    private val phoneRegex = "^\\+?[0-9]{9,15}\$".toRegex()

    /**
     * Kiểm tra định dạng Email hợp lệ.
     */
    fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }

    /**
     * Kiểm tra định dạng Số điện thoại hợp lệ (9-15 chữ số, hỗ trợ có dấu + phía trước).
     */
    fun isValidPhone(phone: String): Boolean {
        return phoneRegex.matches(phone)
    }

    /**
     * Kiểm tra định dạng Email HOẶC Số điện thoại hợp lệ.
     */
    fun isValidEmailOrPhone(input: String): Boolean {
        return isValidEmail(input) || isValidPhone(input)
    }
}
