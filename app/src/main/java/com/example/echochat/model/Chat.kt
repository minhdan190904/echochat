package com.example.echochat.model

import com.example.echochat.util.myUser
import java.util.Date

data class Chat(
    val user1: User,
    val user2: User,
    var id: Int? = null,
    val timeCreated: Date? = null,
    val messageList: MutableList<Message> = mutableListOf()
) {

    fun getLastMessage(): Message? = messageList.lastOrNull()

    fun getChatTitle(currentUser: User): String {
        return if (currentUser.id == user1.id) user2.name else user1.name
    }

    fun getChatImage(currentUser: User): String? {
        return if (currentUser.id == user1.id) user2.profileImageUrl else user1.profileImageUrl
    }

    fun getOtherUser(currentUser: User): User {
        return if (currentUser.id == user1.id) user2 else user1
    }

    fun getChatTitle(): String {
        return if (myUser?.id == user1.id) user2.name else user1.name
    }
}
