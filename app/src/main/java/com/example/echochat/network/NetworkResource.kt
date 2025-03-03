package com.example.echochat.network

sealed class NetworkResource<out T> {
    data class Success<T>(val data: T) : NetworkResource<T>()
    data class Error<T>(val message: String? = "", val responseCode: Int? = null) : NetworkResource<T>()
    data class NetworkException<T>(val message: String?) : NetworkResource<T>()

}