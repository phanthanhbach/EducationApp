package com.example.educationapp.core.network

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import co.touchlab.kermit.Logger

suspend inline fun <reified T> safeApiCall(
    crossinline apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (e: ClientRequestException) {
        val errorMsg = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            e.message
        }
        Logger.e(e) { "API Client Request Error (code: ${e.response.status.value}): $errorMsg" }
        ApiResult.Error.HttpError(
            code = e.response.status.value,
            message = errorMsg,
            exception = e
        )
    } catch (e: ServerResponseException) {
        Logger.e(e) { "API Server Error (code: ${e.response.status.value})" }
        ApiResult.Error.HttpError(
            code = e.response.status.value,
            message = "Server error occurred. Please try again later.",
            exception = e
        )
    } catch (e: RedirectResponseException) {
        Logger.e(e) { "API Redirect Error (code: ${e.response.status.value})" }
        ApiResult.Error.HttpError(
            code = e.response.status.value,
            message = "Redirect error occurred.",
            exception = e
        )
    } catch (e: SerializationException) {
        Logger.e(e) { "API Serialization Error: ${e.message}" }
        ApiResult.Error.SerializationError(
            message = "Failed to parse data from server.",
            exception = e
        )
    } catch (e: Exception) {
        val isNetworkIssue = e is ConnectTimeoutException ||
                e is HttpRequestTimeoutException ||
                e::class.simpleName?.contains("IOException", ignoreCase = true) == true ||
                e::class.simpleName?.contains("ConnectException", ignoreCase = true) == true

        if (isNetworkIssue) {
            Logger.e(e) { "API Network Connection Error" }
            ApiResult.Error.NetworkError(
                message = "No internet connection. Please check your network.",
                exception = e
            )
        } else {
            Logger.e(e) { "API Unknown Error: ${e.message}" }
            ApiResult.Error.UnknownError(
                message = e.message ?: "An unknown error occurred.",
                exception = e
            )
        }
    }
}
