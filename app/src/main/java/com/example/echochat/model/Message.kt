package com.example.echochat.model

data class Message(
    val sender: User? = null,
    var message: String = "",
    val sendingTime: String = "",
    val isSeen: Boolean,
    val id: Int? = null,
    val messageType: MessageType = MessageType.TEXT
){
    enum class MessageType {
        TEXT,
        IMAGE,
        VIDEO
    }
}
