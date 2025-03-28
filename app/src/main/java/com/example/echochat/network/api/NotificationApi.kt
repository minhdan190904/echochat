package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.dto.NotificationRequest
import com.example.echochat.model.dto.UserDeviceToken
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
    @POST("/notifications/send/{userId}")
    suspend fun sendNotification(@Path("userId") userId: Int, @Body notificationRequest: NotificationRequest): ResResponse<List<UserDeviceToken>>
}