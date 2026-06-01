package com.example.educationapp.core.network

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>

    sealed interface Error : ApiResult<Nothing> {
        val exception: Throwable
        val message: String?

        data class HttpError(
            val code: Int,
            override val message: String?,
            override val exception: Throwable
        ) : Error

        data class NetworkError(
            val isTimeout: Boolean,
            override val message: String?,
            override val exception: Throwable
        ) : Error

        data class SerializationError(
            override val message: String?,
            override val exception: Throwable
        ) : Error

        data class UnknownError(
            override val message: String?,
            override val exception: Throwable
        ) : Error
    }
}