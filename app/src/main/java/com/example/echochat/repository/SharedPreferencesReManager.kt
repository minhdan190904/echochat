package com.example.echochat.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.echochat.util.LOCAL_SHARED_PREF
import com.example.echochat.util.TOKEN_KEY
import com.google.gson.Gson

object  SharedPreferencesReManager {
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context){
        prefs = context.getSharedPreferences(LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }
    fun<T> saveData(key: String, data: T) {
        prefs.edit().putString(key, gson.toJson(data)).apply()
    }

    fun<T> getData(key: String, clazz: Class<T>): T? {
        val json = prefs.getString(key, null) ?: return null
        return gson.fromJson(json, clazz)
    }

    fun clearData(key: String) {
        prefs.edit().remove(key).apply()
    }
}
