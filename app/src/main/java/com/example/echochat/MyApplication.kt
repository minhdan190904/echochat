package com.example.echochat

import android.app.Application
import com.example.echochat.network.TokenManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(applicationContext)
    }
}