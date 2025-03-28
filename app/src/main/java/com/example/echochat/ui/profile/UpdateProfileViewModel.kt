package com.example.echochat.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.FileRepository
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.UiState
import com.example.echochat.util.formatOnlyDate
import com.example.echochat.util.toDate
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.Date

class UpdateProfileViewModel(
    private val fileRepository: FileRepository = FileRepository(),
    private val userRepository: UserRepository = UserRepository()
): ViewModel() {
    private val _updateUiState = MutableLiveData<UiState<Nothing>>()
    val updateUiState: LiveData<UiState<Nothing>> = _updateUiState

    private val _updateAvatarUiState = MutableLiveData<UiState<Nothing>>()
    val updateAvatarUiState: LiveData<UiState<Nothing>> = _updateAvatarUiState

    val name = MutableLiveData<String>(fileRepository.myUser?.name)
    val email = MutableLiveData<String>(fileRepository.myUser?.email)
    val profileImageUrl = MutableLiveData<String>(fileRepository.myUser?.profileImageUrl)
    val birthday = MutableLiveData<String>(fileRepository.myUser?.birthday?.formatOnlyDate())

    fun updateProfile() {
        viewModelScope.launch {
            _updateUiState.value = UiState.Loading
            val nameUser: String = name.value!!
            val emailUser: String = email.value!!
            val profileImageUrlUser: String = profileImageUrl.value!!
            val birthdayUser: Date = birthday.value!!.toDate()!!
            val user: User = fileRepository.myUser!!.copy(name = nameUser, email = emailUser, profileImageUrl = profileImageUrlUser, birthday = birthdayUser)
            val response =  userRepository.updateUser(user)
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