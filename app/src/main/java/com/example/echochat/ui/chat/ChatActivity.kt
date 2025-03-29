package com.example.echochat.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.echochat.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}