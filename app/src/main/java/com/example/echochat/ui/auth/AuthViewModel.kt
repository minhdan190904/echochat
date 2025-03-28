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
import com.example.echochat.repository.AuthRepository
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.TOKEN_KEY
import com.example.echochat.util.TOKEN_USER_DEVICE
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {
    private val _register = MutableLiveData<UiState<User>>()
    val register: LiveData<UiState<User>> = _register

    private val _login = MutableLiveData<UiState<ResLoginDTO>>()
    val login: LiveData<UiState<ResLoginDTO>> = _login

    private val _logout = MutableLiveData<String>()
    val logout: LiveData<String> = _logout

    fun register(
        email: String,
        password: String,
        name: String,
    ) {
        viewModelScope.launch {
            _register.value = UiState.Loading
            try {
                val resResponse = repository.registerUser(
                    RegisterDTO(
                        username = email,
                        password = password,
                        name = name
                    )
                )
                when (resResponse.statusCode) {
                    201 -> {
                        _register.value = UiState.Success(resResponse.data)
                    }
                    409 -> {
                        _register.value = UiState.Failure("Email already exists")
                    }
                    else -> {
                        _register.value = UiState.Failure(resResponse.error)
                    }
                }
            } catch (ex: HttpException) {
                _register.value = UiState.Failure(
                    when (ex.code()) {
                        409 -> "Email already exists"
                        500 -> "Internal Server Error. Please try again later."
                        else -> "Server error: ${ex.message()}"
                    }
                )
            } catch (ex: IOException) {
                _register.value = UiState.Failure("Network error. Please check your connection.")
            } catch (ex: Exception) {
                _register.value = UiState.Failure(ex.message ?: "Unexpected error")
            }
        }
    }

    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _login.value = UiState.Loading
            try {
                val resResponse = repository.loginUser(
                    LoginDTO(
                        username = email,
                        password = password
                    )
                )

                when (resResponse.statusCode) {
                    200 -> {
                        _login.value = UiState.Success(resResponse.data)
                        SharedPreferencesReManager.saveData(TOKEN_KEY, resResponse.data.token)
                        SharedPreferencesReManager.saveData(USER_SESSION, resResponse.data.user)
                        Log.i("MYTAG", resResponse.data.user.toString())
                        try {
                            val temp = repository.createUserDeviceToken(UserDeviceToken(
                                token = TOKEN_USER_DEVICE,
                                user = resResponse.data.user,
                                timeStamp = LocalDateTime.now().toString())
                            )
                        } catch (ex: Exception) {
                            Log.i("MYTAG", ex.message.toString())
                        }
                        MY_USER_ID = resResponse.data.user.id!!
                    }
                    401 -> {
                        _login.value = UiState.Failure("Username or password is incorrect")
                    }
                    else -> {
                        _login.value = UiState.Failure(resResponse.error)
                    }
                }
            } catch (ex: HttpException) {
                _login.value = UiState.Failure(
                    when (ex.code()) {
                        401 -> "Username or password is incorrect"
                        500 -> "Internal Server Error. Please try again later."
                        else -> "Server error: ${ex.message()}"
                    }
                )
            } catch (ex: IOException) {
                _login.value = UiState.Failure("Network error. Please check your connection.")
            } catch (ex: Exception) {
                _login.value = UiState.Failure(ex.message ?: "Unexpected error")
            }
        }
    }

    fun logout() {
        SharedPreferencesReManager.clearData(TOKEN_KEY)
        SharedPreferencesReManager.clearData(USER_SESSION)
        Log.i("LOGOUT", "LOGOUT")
        MY_USER_ID = 1
        _logout.value = "Logout"
    }

    fun getSession(result: (User?) -> Unit) {
        val user = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
        if(user != null){
            MY_USER_ID = user.id!!
            result.invoke(user)
        } else {
            Log.i("SESSION_TAG", "12321")
        }
    }

}