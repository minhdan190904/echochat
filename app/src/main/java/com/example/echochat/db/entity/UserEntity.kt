package com.example.echochat.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val birthday: Date?,
    val profileImageUrl: String?,
    val isOnline: Boolean,
    val lastSeen: Date?
)