package com.example.echochat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.echochat.db.coverter.Converters
import com.example.echochat.db.dao.ChatDao
import com.example.echochat.db.dao.MessageDao
import com.example.echochat.db.dao.UserDao
import com.example.echochat.db.entity.ChatEntity
import com.example.echochat.db.entity.MessageEntity
import com.example.echochat.db.entity.UserEntity

@Database(entities = [UserEntity::class, ChatEntity::class, MessageEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}