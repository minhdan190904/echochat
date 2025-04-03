package com.example.echochat.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.AuthRepository
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.TOKEN_KEY
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.UiState
import com.example.echochat.util.tokenUserDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _register = MutableLiveData<UiState<User>>()
    val register: LiveData<UiState<User>> = _register

    private val _login = MutableLiveData<UiState<ResLoginDTO>>()
    val login: LiveData<UiState<ResLoginDTO>> = _login

    fun register(
        email: String,
        password: String,
        name: String,
    ) {
        viewModelScope.launch {
            _register.value = UiState.Loading
            val response = authRepository.registerUser(
                RegisterDTO(
                    username = email,
                    password = password,
                    name = name
                )
            )
            when (response) {
                is NetworkResource.Success -> {
                    _register.value = UiState.Success(response.data)
                }

                is NetworkResource.NetworkException -> {
                    _register.value = UiState.Failure(response.message)
                }

                is NetworkResource.Error -> {
                    _register.value = UiState.Failure(response.message)
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _login.value = UiState.Loading
            val response = authRepository.loginUser(
                LoginDTO(
                    username = email,
                    password = password
                )
            )
            when (response) {
                is NetworkResource.Success -> {
                    SharedPreferencesReManager.saveData(TOKEN_KEY, response.data.token)
                    SharedPreferencesReManager.saveData(USER_SESSION, response.data.user)
                    Log.i("MYTAG", response.data.user.toString())
                    try {
                        val temp = authRepository.createUserDeviceToken(UserDeviceToken(
                            token = tokenUserDevice!!,
                            user = response.data.user,
                            timeStamp = LocalDateTime.now().toString())
                        )
                    } catch (ex: Exception) {
                        Log.i("MYTAG", ex.message.toString())
                    }
                    _login.value = UiState.Success(response.data)
                }

                is NetworkResource.NetworkException -> {
                    _login.value = UiState.Failure(response.message)
                }

                is NetworkResource.Error -> {
                    _login.value = UiState.Failure(response.message)
                }
            }
        }
    }

    fun getSession(result: (User?) -> Unit) {
        val user = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
        if(user != null){
            result.invoke(user)
        } else {
            Log.i("SESSION_TAG", "12321")
        }
    }

}