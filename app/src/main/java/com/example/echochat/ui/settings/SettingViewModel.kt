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
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.FileRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class SettingViewModel: ViewModel() {
    private val fileRepository = FileRepository(ApiClient.apiService)

    private val _imageUrl: MutableLiveData<String> = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    fun uploadAvatarImage(file: MultipartBody.Part){
        viewModelScope.launch {
            val response = fileRepository.uploadFile(file, "avatar")
            when (response) {
                is NetworkResource.Success -> {
                    _imageUrl.value = response.data.pathToFile
                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("ErrorFile", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("ErrorFile", it) }
                }

                else -> {
                    Log.i("ErrorFile", "Error")
                }
            }
        }
    }

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
}