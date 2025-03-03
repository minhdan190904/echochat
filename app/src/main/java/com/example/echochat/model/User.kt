package com.example.echochat.model

data class User(
    val id: Int? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profileImageUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: String? = null
)