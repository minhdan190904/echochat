package com.example.echochat.model.dto

import com.example.echochat.model.User

data class ResLoginDTO(
    val token: String,
    val user: User
)