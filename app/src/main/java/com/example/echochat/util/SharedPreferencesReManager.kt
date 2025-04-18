package com.example.echochat.util

import android.content.Context
import android.content.SharedPreferences
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

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
