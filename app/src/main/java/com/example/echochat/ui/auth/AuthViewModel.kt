package com.example.echochat.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.LoginDTO
import com.example.echochat.model.RegisterDTO
import com.example.echochat.model.ResLoginDTO
import com.example.echochat.model.User
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.AuthRepository
import com.example.echochat.util.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel: ViewModel() {
    private val repository: AuthRepository = AuthRepository(ApiClient.apiService)
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
                    }
                    401 -> {
                        _login.value = UiState.Failure("Username or password is incorrect")
                    }
                    else -> {
                        _login.value = UiState.Failure(resResponse.error)
                    }
                }
            } catch (ex: HttpException) {
                _register.value = UiState.Failure(
                    when (ex.code()) {
                        401 -> "Username or password is incorrect"
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

}