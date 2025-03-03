package com.example.echochat.model

data class Message(
    val sender: User? = null,
    val message: String = "",
    val sendingTime: String = "",
    val id: Int? = null,
    val type: MessageType = MessageType.TEXT
){
    enum class MessageType {
        TEXT,
        IMAGE
    }
}
