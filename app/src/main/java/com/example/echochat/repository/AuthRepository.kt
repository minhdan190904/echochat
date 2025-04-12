package com.example.echochat.repository

import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.AuthApi
import com.example.echochat.util.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun registerUser(registerDTO: RegisterDTO): NetworkResource<User> {
        return handleNetworkCall(
            call = { authApi.registerUser(registerDTO).data },
            customErrorMessages = mapOf(
                409 to "Email already exists"
            )
        )
    }

    suspend fun loginUser(loginDTO: LoginDTO): NetworkResource<ResLoginDTO> {
        return handleNetworkCall(
            call = { authApi.loginUser(loginDTO).data },
            customErrorMessages = mapOf(
                401 to "Username or password is incorrect"
            )
        )
    }

    suspend fun createUserDeviceToken(userDeviceToken: UserDeviceToken): NetworkResource<UserDeviceToken> {
        return handleNetworkCall(
            call = { authApi.createUserDeviceToken(userDeviceToken).data }
        )
    }

    suspend fun deleteByToken(token: String): NetworkResource<Int> {
        return handleNetworkCall(
            call = { authApi.deleteByToken(token).data }
        )
    }

}