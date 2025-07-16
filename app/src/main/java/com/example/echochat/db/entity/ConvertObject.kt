package com.example.echochat.db.entity

import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.User

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id ?: throw IllegalStateException("User ID cannot be null"),
        name = name,
        email = email,
        birthday = birthday,
        profileImageUrl = profileImageUrl,
        isOnline = isOnline,
        lastSeen = lastSeen
    )
}


fun UserEntity.toModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        birthday = birthday,
        profileImageUrl = profileImageUrl,
        isOnline = isOnline,
        lastSeen = lastSeen
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        id = id ?: throw IllegalStateException("Chat ID cannot be null"),
        user1Id = user1.id ?: throw IllegalStateException("User1 ID cannot be null"),
        user2Id = user2.id ?: throw IllegalStateException("User2 ID cannot be null"),
        timeCreated = timeCreated
    )
}

fun Message.toEntity(chatId: Int): MessageEntity {
    return MessageEntity(
        id = id, // ID từ backend, có thể null nếu chưa đồng bộ
        chatId = chatId,
        senderId = sender?.id ?: throw IllegalStateException("Sender ID cannot be null"),
        message = message,
        sendingTime = sendingTime,
        isSeen = isSeen,
        messageType = messageType.name,
        isUploading = isUploading,
        idLoading = idLoading
    )
}

fun MessageEntity.toModel(sender: User? = null): Message {
    return Message(
        sender = sender,
        message = message,
        sendingTime = sendingTime,
        isSeen = isSeen,
        id = id,
        messageType = Message.MessageType.valueOf(messageType),
        isUploading = isUploading,
        idLoading = idLoading
    )
}

fun MessageEntity.toModelNullId(sender: User? = null): Message {
    return Message(
        sender = sender,
        message = message,
        sendingTime = sendingTime,
        isSeen = isSeen,
        id = null,
        messageType = Message.MessageType.valueOf(messageType),
        isUploading = isUploading,
        idLoading = idLoading
    )
}