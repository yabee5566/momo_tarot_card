package com.onean.momo.ui

sealed interface UiError {
    data class ServerResponseError(val message: String) : UiError
    data object NetworkError : UiError
    data object SessionNotFoundError : UiError
}
