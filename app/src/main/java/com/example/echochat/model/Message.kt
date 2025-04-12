package com.example.echochat.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(
    val sender: User? = null,
    var message: String = "",
    val sendingTime: Date? = null,
    @SerializedName("isSeen") var isSeen: Boolean = false,
    val id: Int? = null,
    val messageType: MessageType = MessageType.TEXT,
    var isUploading: Boolean = false,
    var idLoading: String? = null
){
    enum class MessageType {
        TEXT,
        IMAGE,
        VIDEO
    }
}
