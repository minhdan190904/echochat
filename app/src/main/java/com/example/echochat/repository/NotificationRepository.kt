package com.example.echochat.repository

import com.example.echochat.model.dto.NotificationRequest
import com.example.echochat.model.dto.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.NotificationApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi
){
    suspend fun sendMessageNotification(receiverId: Int, notificationRequest: NotificationRequest): NetworkResource<List<UserDeviceToken>> {
        return try {
            val response = notificationApi.sendNotification(receiverId, notificationRequest)
            NetworkResource.Success(response.data)
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