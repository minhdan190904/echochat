package com.example.echochat.repository;

import com.example.echochat.model.User
import com.example.echochat.model.dto.FriendRequestDTO
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.FriendRequestApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.getFriend
import com.example.echochat.util.myUser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRequestRepository @Inject constructor(
    private val friendRequestApi: FriendRequestApi
){

    suspend fun sentFriendRequest(friendRequestDTO: FriendRequestDTO): NetworkResource<FriendRequestDTO> {
        return try {
            val friendRequestApi = friendRequestApi.sendFriendRequest(friendRequestDTO)
            NetworkResource.Success(friendRequestApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
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

    suspend fun getUserRequests(query: String?): NetworkResource<List<FriendRequestDTO>> {
        return try {
            val friendRequestListApi = friendRequestApi.getFriendRequests(myUser?.id!!)
            if (query.isNullOrEmpty()) {
                NetworkResource.Success(friendRequestListApi.data)
            } else {
                NetworkResource.Success(friendRequestListApi.data.filter {
                    it.getFriend().name.contains(query, true)
                })
            }
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
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
