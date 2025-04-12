package com.example.echochat.network.api

import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.ResResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {
    @GET("/chats/{id}")
    suspend fun fetchChatById(@Path("id") id: Int): ResResponse<Chat>

    @GET("/chats/user/{userId}")
    suspend fun fetchAllChatByUserId(@Path("userId") id: Int): ResResponse<List<Chat>>

    @GET("/chats/between/{user1Id}/{user2Id}")
    suspend fun fetchChatIdByUserIds(
        @Path("user1Id") user1Id: Int,
        @Path("user2Id") user2Id: Int
    ): ResResponse<Int>

    @PUT("/chats/add_message")
    suspend fun addMessage(
        @Query("chatId") chatId: Int,
        @Body message: Message
    ): ResResponse<Chat>

    @PUT("/chats/send_message")
    suspend fun sendMessage(
        @Query("chatId") chatId: Int,
        @Body message: Message
    ): ResResponse<Message>

    @PUT("/chats/updateSeenLastMessage")
    suspend fun updateSeenLastMessage(
        @Query("chatId") chatId: Int
    ): ResResponse<Chat>
}