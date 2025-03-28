package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("/users/{id}")
    suspend fun fetchUserById(@Path("id") id: Int): ResResponse<User>

    @PUT("/users/update")
    suspend fun updateUser(@Body user: User): ResResponse<User>

    @GET("/users")
    suspend fun fetchAllUser(): ResResponse<List<User>>
}