package com.example.echochat.util
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object HasData : UiState<Nothing>()
    data object NoData : UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Failure(val error: String?): UiState<Nothing>()
}
