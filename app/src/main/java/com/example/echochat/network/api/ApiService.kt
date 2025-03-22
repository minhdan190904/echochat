package com.example.echochat.network.api

import com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.model.LoginDTO
import com.example.echochat.model.Message
import com.example.echochat.model.NotificationRequest
import com.example.echochat.model.RegisterDTO
import com.example.echochat.model.ResLoginDTO
import com.example.echochat.model.ResResponse
import com.example.echochat.model.ResUpLoadFileDTO
import com.example.echochat.model.User
import com.example.echochat.model.UserDeviceToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("/users")
    suspend fun fetchAllUser(): ResResponse<List<User>>

    @POST("/register")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): ResResponse<User>

    @POST("/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): ResResponse<ResLoginDTO>

    @PUT("/users")
    suspend fun updateUser(@Body user: User): ResResponse<User>

    @GET("/chats")
    suspend fun fetchAllChats(): ResResponse<List<Chat>>

    @GET("/chats/{id}")
    suspend fun fetchChatById(@Path("id") id: Int): ResResponse<Chat>

    @GET("/all_chats/{userId}")
    suspend fun fetchAllChatByUserId(@Path("userId") id: Int): ResResponse<List<Chat>>

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

    @POST("/friends")
    suspend fun sendFriendRequest(@Body friendRequestDTO: FriendRequestDTO): ResResponse<FriendRequestDTO>

    @Multipart
    @POST("/api/v1/files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Query("folder") folder: String,
    ): ResResponse<ResUpLoadFileDTO>

    @POST("user_device_token/create")
    suspend fun createUserDeviceToken(@Body userDeviceToken: UserDeviceToken): ResResponse<UserDeviceToken>

    @POST("notification/send/{userId}")
    suspend fun sendNotification(@Path("userId") userId: Int, @Body notificationRequest: NotificationRequest): ResResponse<List<UserDeviceToken>>


}