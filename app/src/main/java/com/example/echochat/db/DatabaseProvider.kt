package com.example.echochat.db

import android.content.Context
import androidx.room.Room
import com.example.echochat.db.dao.ChatDao
import com.example.echochat.db.dao.MessageDao
import com.example.echochat.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProvider {
    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ChatDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: ChatDatabase): MessageDao {
        return database.messageDao()
    }
}