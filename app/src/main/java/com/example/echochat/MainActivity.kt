package com.example.echochat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.echochat.util.TOKEN_USER_DEVICE
import com.example.echochat.util.toast
import com.example.echochat.util.tokenUserDevice
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set the splash screen
        installSplashScreen()

        // Set the no dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view
        setContentView(R.layout.activity_main)

        // Get the token for the user device
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.i("MYTAG", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                if(token != null){
                    tokenUserDevice = token
                } else {
                    toast(getString(R.string.notification_not_available))
                }
                Log.i("MYTAG", "FCM Token: $token")
            }
    }
}