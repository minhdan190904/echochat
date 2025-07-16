package com.example.echochat.model.dto

import com.example.echochat.model.Message
import com.fasterxml.jackson.annotation.JsonFormat
import com.google.gson.annotations.SerializedName
import java.util.Date

data class MessageDTO(
    val id: Int? = null,
    val message: String = "",
    val senderId: Int? = null,
    val receiverId: Int? = null,
    val chatId: Int? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a", timezone = "GMT+07:00")
    val sendingTime: Date? = null,
    val messageType: String? = Message.MessageType.TEXT.name,
    @SerializedName("isSeen") var isSeen: Boolean = false,
)