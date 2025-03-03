package com.example.echochat.repository

import com.example.echochat.model.LoginDTO
import com.example.echochat.model.RegisterDTO
import com.example.echochat.model.ResLoginDTO
import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import com.example.echochat.network.api.ApiService
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

class AuthRepository(private val apiService: ApiService) {
    suspend fun registerUser(registerDTO: RegisterDTO): ResResponse<User>{
        return apiService.registerUser(registerDTO)
    }

    suspend fun loginUser(loginDTO: LoginDTO): ResResponse<ResLoginDTO>{
        return apiService.loginUser(loginDTO)
    }

}