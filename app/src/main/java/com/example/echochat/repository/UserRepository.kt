package com.example.echochat.repository

import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient.userApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import retrofit2.HttpException
import java.io.IOException

class UserRepository {
    var myUser: User? = null

    init {
        myUser = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    }

    suspend fun fetchUserById(id: Int): NetworkResource<User> {
        return try {
            val userApi = userApi.fetchUserById(id)
            NetworkResource.Success(userApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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

    suspend fun updateUser(user: User): NetworkResource<User> {
        return try {
            val userApi = userApi.updateUser(user)
            SharedPreferencesReManager.saveData(USER_SESSION, userApi.data)
            myUser = userApi.data
            NetworkResource.Success(userApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
                    500 -> "Internal Server Error. Please try again later."
                    400 ->  ex.message
                    else -> "Server error: ${ex.code()}"
                }, responseCode = ex.code()
            )
        } catch (ex: IOException) {
            NetworkResource.NetworkException("Network error. Please check your connection.")
        } catch (ex: Exception) {
            NetworkResource.Error(ex.message ?: "Unexpected error")
        }
    }


}