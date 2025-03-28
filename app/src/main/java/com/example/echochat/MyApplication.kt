package com.example.echochat

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesReManager.init(applicationContext)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageCode = sharedPreferences.getString("language", "en") ?: "en"
        LocaleHelper.setLocale(languageCode)
    }
}