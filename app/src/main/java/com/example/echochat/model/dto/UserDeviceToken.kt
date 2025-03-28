package com.example.echochat.model.dto

import com.example.echochat.model.User

data class UserDeviceToken(
    val token: String,
    val id: Int? = null,
    val timeStamp: String? = null,
    val user: User? = null
)