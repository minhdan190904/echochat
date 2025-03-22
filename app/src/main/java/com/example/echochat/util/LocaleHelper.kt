package com.example.echochat.util

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {
    fun setLocale(languageCode: String){
        Log.i("MYTAG", "Locale set to before: ${Locale.getDefault().language}")
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        Log.i("MYTAG", "Locale set to after: ${Locale.getDefault().language}")
    }
}