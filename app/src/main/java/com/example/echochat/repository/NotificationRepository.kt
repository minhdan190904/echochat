package com.example.echochat.repository

import com.example.echochat.model.dto.NotificationRequest
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.NotificationApi
import com.example.echochat.util.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
){
    suspend fun sendMessageNotification(receiverId: Int, notificationRequest: NotificationRequest): NetworkResource<List<UserDeviceToken>> {
        return handleNetworkCall(
            call = { notificationApi.sendNotification(receiverId, notificationRequest).data },
            customErrorMessages = mapOf(
                400 to "Error sending message notification"
            )
        )
    }
}