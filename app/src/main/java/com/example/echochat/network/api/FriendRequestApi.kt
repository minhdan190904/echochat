package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.dto.FriendRequestDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendRequestApi {
    @GET("/friend_requests/user/{userId}")
    suspend fun getFriendRequests(@Path("userId") userId: Int): ResResponse<List<FriendRequestDTO>>

    @POST("/friend_requests/send")
    suspend fun sendFriendRequest(@Body friendRequestDTO: FriendRequestDTO): ResResponse<FriendRequestDTO>
}