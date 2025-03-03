package com.example.echochat.model

class GroupChat(
    val groupName: String,
    val groupImage: String?,
    val userList: MutableList<User>
): Chat() {
    override fun getChatTitle(): String {
        return groupName
    }

    override fun getChatImage(): String? {
        return groupImage
    }
}