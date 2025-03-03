package com.example.echochat.model


class NormalChat(val receiver: User): Chat() {
    override fun getChatTitle(): String {
        return receiver.name
    }

    override fun getChatImage(): String? {
        return receiver.profileImageUrl
    }

}