package com.example.echochat.model

abstract class Chat(var id: Int? = null) {
    val messageList = mutableListOf<Message>()

    fun addMessage(message: Message){
        messageList.add(message)
    }

    fun getLastMessage(): Message? = messageList.lastOrNull()

    abstract fun getChatTitle(): String
    abstract fun getChatImage(): String?

}