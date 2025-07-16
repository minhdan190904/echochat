package com.example.echochat.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatId", "sendingTime"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val chatId: Int,
    val senderId: Int,
    val message: String,
    val sendingTime: Date?,
    val isSeen: Boolean,
    val messageType: String,
    val isUploading: Boolean,
    val idLoading: String?
)