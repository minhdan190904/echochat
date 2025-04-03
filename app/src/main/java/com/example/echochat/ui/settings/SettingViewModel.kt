package com.example.echochat.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.repository.AuthRepository
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.tokenUserDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _logout = MutableLiveData<String>()
    val logout: LiveData<String> = _logout

    fun openAppNotificationSettings(context: Context) {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", context.packageName)
                    putExtra("app_uid", context.applicationInfo.uid)
                }

                else -> {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:" + context.packageName)
                }
            }
        }
        context.startActivity(intent)
    }

    fun logout() {
        viewModelScope.launch {
            if(tokenUserDevice != null){
                val response = authRepository.deleteByToken(tokenUserDevice!!)
            }
            SharedPreferencesReManager.clearAllData()
            Log.i("LOGOUT", "LOGOUT")
            _logout.value = "Bạn đã đăng xuất"
        }
    }
}