package com.example.echochat.model

import java.lang.Error

data class ResResponse<T>(
    val statusCode: Int,
    val error: String,
    val data: T,
    val message: Any
)