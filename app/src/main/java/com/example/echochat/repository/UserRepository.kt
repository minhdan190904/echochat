package com.example.echochat.repository

import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.UserApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.handleNetworkCall
import com.example.echochat.util.myUser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun fetchUserById(id: Int): NetworkResource<User> {
        return handleNetworkCall(
            call = { userApi.fetchUserById(id).data },
            customErrorMessages = mapOf(
                400 to "Error fetching user"
            )
        )
    }

    suspend fun updateUser(user: User): NetworkResource<User> {
        return handleNetworkCall(
            call = {
                val userApi = userApi.updateUser(user)
                myUser = userApi.data
                userApi.data
            },
            customErrorMessages = mapOf(
                400 to "Error updating user"
            )
        )
    }
}