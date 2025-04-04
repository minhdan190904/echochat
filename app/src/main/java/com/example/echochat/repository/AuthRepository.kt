package com.example.echochat.repository

import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.model.dto.RegisterDTO
import com.example.echochat.model.dto.ResLoginDTO
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.AuthApi
import com.example.echochat.util.UiState
import com.example.echochat.util.handleNetworkCall
import com.example.echochat.util.myUser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
//    suspend fun registerUser(registerDTO: RegisterDTO): NetworkResource<User>{
//        return try {
//            val registerUserApi = authApi.registerUser(registerDTO)
//            NetworkResource.Success(registerUserApi.data)
//        } catch (ex: HttpException) {
//            NetworkResource.Error(
//                message = when (ex.code()) {
//                    409 -> "Email already exists"
//                    500 -> "Internal Server Error. Please try again later."
//                    else -> "Server error: ${ex.message()}"
//                }, responseCode = ex.code()
//            )
//        } catch (ex: IOException) {
//            NetworkResource.NetworkException("Network error. Please check your connection.")
//        } catch (ex: Exception) {
//            NetworkResource.Error(ex.message ?: "Unexpected error")
//        }
//    }

    suspend fun registerUser(registerDTO: RegisterDTO): NetworkResource<User> {
        return handleNetworkCall(
            call = { authApi.registerUser(registerDTO).data },
            customErrorMessages = mapOf(
                409 to "Email already exists"
            )
        )
    }

//    suspend fun loginUser(loginDTO: LoginDTO): NetworkResource<ResLoginDTO>{
//        return try {
//            val resLoginDTOApi = authApi.loginUser(loginDTO)
//            NetworkResource.Success(resLoginDTOApi.data)
//        } catch (ex: HttpException) {
//            NetworkResource.Error(
//                message = when (ex.code()) {
//                    401 -> "Username or password is incorrect"
//                    500 -> "Internal Server Error. Please try again later."
//                    else -> "Server error: ${ex.message()}"
//                }, responseCode = ex.code()
//            )
//        } catch (ex: IOException) {
//            NetworkResource.NetworkException("Network error. Please check your connection.")
//        } catch (ex: Exception) {
//            NetworkResource.Error(ex.message ?: "Unexpected error")
//        }
//    }

    suspend fun loginUser(loginDTO: LoginDTO): NetworkResource<ResLoginDTO> {
        return handleNetworkCall(
            call = { authApi.loginUser(loginDTO).data },
            customErrorMessages = mapOf(
                401 to "Username or password is incorrect"
            )
        )
    }

    suspend fun createUserDeviceToken(userDeviceToken: UserDeviceToken): NetworkResource<UserDeviceToken> {
        return try {
            val userDeviceTokenApi = authApi.createUserDeviceToken(userDeviceToken)
            NetworkResource.Success(userDeviceTokenApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    401 -> "Username or password is incorrect"
                    500 -> "Internal Server Error. Please try again later."
                    else -> "Server error: ${ex.message()}"
                }, responseCode = ex.code()
            )
        } catch (ex: IOException) {
            NetworkResource.NetworkException("Network error. Please check your connection.")
        } catch (ex: Exception) {
            NetworkResource.Error(ex.message ?: "Unexpected error")
        }
    }

    suspend fun deleteByToken(token: String): NetworkResource<Int> {
        return try {
            val codeResponse = authApi.deleteByToken(token)
            NetworkResource.Success(codeResponse.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    401 -> "Username or password is incorrect"
                    500 -> "Internal Server Error. Please try again later."
                    else -> "Server error: ${ex.message()}"
                }, responseCode = ex.code()
            )
        } catch (ex: IOException) {
            NetworkResource.NetworkException("Network error. Please check your connection.")
        } catch (ex: Exception) {
            NetworkResource.Error(ex.message ?: "Unexpected error")
        }
    }

}