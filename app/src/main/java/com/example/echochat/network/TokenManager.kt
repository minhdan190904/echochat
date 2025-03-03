package com.example.echochat.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private lateinit var prefs: SharedPreferences
    fun init(context: Context){
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    fun saveToken(token: String) {
        prefs.edit().putString("TOKEN_KEY", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("TOKEN_KEY", null)
    }

    fun clearToken() {
        prefs.edit().remove("TOKEN_KEY").apply()
    }
}
