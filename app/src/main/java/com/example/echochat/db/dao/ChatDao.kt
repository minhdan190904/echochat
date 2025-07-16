package com.example.echochat.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.echochat.db.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("SELECT * FROM chats WHERE user1Id = :userId OR user2Id = :userId ORDER BY timeCreated DESC")
    fun getChatsForUser(userId: Int): Flow<List<ChatEntity>>

    @Query("DELETE FROM chats")
    suspend fun clearChats()
}