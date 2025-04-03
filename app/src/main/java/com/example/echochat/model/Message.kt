package com.example.echochat.model

import com.google.gson.annotations.SerializedName

data class Message(
    val sender: User? = null,
    var message: String = "",
    val sendingTime: String = "",
    @SerializedName("isSeen") var isSeen: Boolean = false,
    val id: Int? = null,
    val messageType: MessageType = MessageType.TEXT
){
    enum class MessageType {
        TEXT,
        IMAGE,
        VIDEO
    }
}
