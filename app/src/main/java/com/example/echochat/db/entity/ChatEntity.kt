package com.example.echochat.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: Int,
    val user1Id: Int,
    val user2Id: Int,
    val timeCreated: Date?,
)