package com.example.echochat.network.api

import com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.model.LoginDTO
import com.example.echochat.model.Message
import com.example.echochat.model.RegisterDTO
import com.example.echochat.model.ResLoginDTO
import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/users")
    suspend fun fetchAllUser(): ResResponse<List<User>>

    @POST("/register")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): ResResponse<User>

    @POST("/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): ResResponse<ResLoginDTO>

    @GET("/chats")
    suspend fun fetchAllChats(): ResResponse<List<Chat>>

    @GET("/chats/{id}")
    suspend fun fetchChatById(@Path("id") id: Int): ResResponse<Chat>

    @PUT("/message")
    suspend fun addMessage(
        @Query("chatId") chatId: Int,
        @Body message: Message
    ): ResResponse<Chat>

    @PUT("/send_message")
    suspend fun sendMessage(
        @Query("chatId") chatId: Int,
        @Body message: Message
    ): ResResponse<Message>

    @GET("/friends/{userId}")
    suspend fun getFriendRequests(@Path("userId") userId: Int): ResResponse<List<FriendRequestDTO>>

}