package com.example.echochat.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.FileRepository
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.UiState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel: ViewModel() {
    private val userRepository = UserRepository(ApiClient.apiService)
    private val fileRepository = FileRepository(ApiClient.apiService)

    private val _updateUiState = MutableLiveData<UiState<Nothing>>()
    val updateUiState: LiveData<UiState<Nothing>> = _updateUiState

    private val _updateAvatarUiState = MutableLiveData<UiState<Nothing>>()
    val updateAvatarUiState: LiveData<UiState<Nothing>> = _updateAvatarUiState

    val name = MutableLiveData<String>(userRepository.myUser?.name)
    val email = MutableLiveData<String>(userRepository.myUser?.email)
    val profileImageUrl = MutableLiveData<String>(userRepository.myUser?.profileImageUrl)

    fun updateProfile() {
        viewModelScope.launch {
            _updateUiState.value = UiState.Loading
            val nameUser: String = name.value!!
            val emailUser: String = email.value!!
            val profileImageUrlUser: String = profileImageUrl.value!!
            val user: User = userRepository.myUser!!.copy(name = nameUser, email = emailUser, profileImageUrl = profileImageUrlUser)
            val response = userRepository.updateUser(user)
            when(response) {
                is NetworkResource.Success -> {
                    _updateUiState.value = UiState.HasData
                }
                is NetworkResource.Error -> {
                    _updateUiState.value = UiState.Failure(response.message)
                }
                is NetworkResource.NetworkException -> {
                    _updateUiState.value = UiState.Failure(response.message)
                }
            }
        }
    }

    fun uploadAvatarImage(file: MultipartBody.Part){
        viewModelScope.launch {
            _updateAvatarUiState.value = UiState.Loading
            val response = fileRepository.uploadFile(file, "avatar")
            when (response) {
                is NetworkResource.Success -> {
                    profileImageUrl.value = response.data.pathToFile
                    _updateAvatarUiState.value = UiState.HasData
                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("ErrorFile", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("ErrorFile", it) }
                }
            }
        }
    }
}