package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/register")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): ResResponse<User>

    @POST("/auth/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): ResResponse<ResLoginDTO>

    @POST("/user_device_tokens/create")
    suspend fun createUserDeviceToken(@Body userDeviceToken: UserDeviceToken): ResResponse<UserDeviceToken>
}