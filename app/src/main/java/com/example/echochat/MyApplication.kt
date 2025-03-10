package com.example.echochat

import android.app.Application
import com.example.echochat.repository.SharedPreferencesReManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesReManager.init(applicationContext)
    }
}