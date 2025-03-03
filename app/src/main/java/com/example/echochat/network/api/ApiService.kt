package com.example.echochat.network.api

import com.example.echochat.model.LoginDTO
import com.example.echochat.model.RegisterDTO
import com.example.echochat.model.ResLoginDTO
import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): ResResponse<User>

    @POST("/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): ResResponse<ResLoginDTO>
}