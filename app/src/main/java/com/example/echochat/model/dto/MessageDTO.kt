package com.example.echochat.model.dto

import com.example.echochat.model.Message

data class MessageDTO(
    val message: Message,
    val receiverId: Int,
    val chatId: Int
)