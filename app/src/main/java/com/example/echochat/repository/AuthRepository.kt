package com.example.echochat.repository

import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient.authApi

class AuthRepository{
    suspend fun registerUser(registerDTO: RegisterDTO): ResResponse<User>{
        return authApi.registerUser(registerDTO)
    }

    suspend fun loginUser(loginDTO: LoginDTO): ResResponse<ResLoginDTO>{
        return authApi.loginUser(loginDTO)
    }

    suspend fun createUserDeviceToken(userDeviceToken: UserDeviceToken): NetworkResource<UserDeviceToken> {
        val userDeviceTokenApi = authApi.createUserDeviceToken(userDeviceToken)
        return NetworkResource.Success(userDeviceTokenApi.data)
    }

}