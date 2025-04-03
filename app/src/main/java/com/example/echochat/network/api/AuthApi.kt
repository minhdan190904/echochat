package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/register")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): ResResponse<User>

    @POST("/auth/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): ResResponse<ResLoginDTO>

    @POST("/user_device_tokens/create")
    suspend fun createUserDeviceToken(@Body userDeviceToken: UserDeviceToken): ResResponse<UserDeviceToken>

    @DELETE("/user_device_tokens/delete")
    suspend fun deleteByToken(@Query("token") token: String): ResResponse<Int>
}