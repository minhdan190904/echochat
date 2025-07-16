package com.example.echochat.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.echochat.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY sendingTime ASC")
    fun getMessagesForChat(chatId: Int): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages")
    suspend fun clearMessages()

    @Query("SELECT * FROM messages WHERE isUploading = 1 OR idLoading IS NOT NULL")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Int): MessageEntity?
}