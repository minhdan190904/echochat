package com.example.echochat.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: Int,
    val user1Id: Int,
    val user2Id: Int,
    val lastMessage: String?,
    val lastMessageTime: Long
)