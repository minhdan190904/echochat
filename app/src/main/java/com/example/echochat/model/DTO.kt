package com.example.echochat.model

data class LoginDTO(
    val username: String,
    val password: String
)

data class RegisterDTO(
    val username: String,
    val password: String,
    val name: String
)

data class ResLoginDTO(
    val token: String,
    val user: User
)

data class MessageDTO(
    val message: Message,
    val idChat: Int
)