package com.example.echochat.model.dto

data class NotificationRequest(
    val title: String,
    val body: String,
    val topic: String,
    val token: String,
    val imageUrl: String
)