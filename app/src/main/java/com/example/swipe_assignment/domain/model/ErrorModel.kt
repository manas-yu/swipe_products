package com.example.swipe_assignment.domain.model

sealed class ErrorModel<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ErrorModel<T>(data)
    class Error<T>(message: String, data: T? = null) : ErrorModel<T>(data, message)
    class Loading<T> : ErrorModel<T>()
}