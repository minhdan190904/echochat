package com.example.echochat.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    val id: Int? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val birthday: Date? = null,
    val profileImageUrl: String? = null,
    @SerializedName("isOnline") var isOnline: Boolean = false,
    val lastSeen: Date? = null
)