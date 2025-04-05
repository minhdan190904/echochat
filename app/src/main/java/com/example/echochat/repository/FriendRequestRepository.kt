package com.example.echochat.repository;

import com.example.echochat.model.User
import com.example.echochat.model.dto.FriendRequestDTO
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.FriendRequestApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.getFriend
import com.example.echochat.util.handleNetworkCall
import com.example.echochat.util.myUser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRequestRepository @Inject constructor(
    private val friendRequestApi: FriendRequestApi
) {

    suspend fun sentFriendRequest(friendRequestDTO: FriendRequestDTO): NetworkResource<FriendRequestDTO> {
        return handleNetworkCall(
            call = { friendRequestApi.sendFriendRequest(friendRequestDTO).data },
            customErrorMessages = mapOf(
                400 to "Error sending friend request",
            )
        )
    }

    suspend fun getUserRequests(query: String?): NetworkResource<List<FriendRequestDTO>> {
        return handleNetworkCall(
            call = {
                val friendRequestListApi = friendRequestApi.getFriendRequests(myUser?.id!!)
                if (query.isNullOrEmpty()) {
                    friendRequestListApi.data
                } else {
                    friendRequestListApi.data.filter {
                        it.getFriend().name.contains(query, true)
                    }
                }
            },
            customErrorMessages = mapOf(
                400 to "Error fetching friend requests"
            )
        )
    }

}
