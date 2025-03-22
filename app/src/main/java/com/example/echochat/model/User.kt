package com.example.echochat.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profileImageUrl: String? = null,
    @SerializedName("isOnline") var isOnline: Boolean = false,
    val lastSeen: String? = null
)