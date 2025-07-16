package com.example.echochat

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.example.echochat.util.ENGLISH_LANGUAGE
import com.example.echochat.util.LANGUAGE_KEY
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesReManager.init(applicationContext)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageCode = sharedPreferences.getString(LANGUAGE_KEY, ENGLISH_LANGUAGE) ?: ENGLISH_LANGUAGE
        LocaleHelper.setLocale(languageCode)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .also { Log.d("MyApplication", "Providing HiltWorkerFactory to WorkManager") }
            .build()
}
