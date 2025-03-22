package com.example.echochat.model
import com.example.echochat.model.FriendRequest.RequestStatus


data class LoginDTO(
    val username: String,
    val password: String
)

data class RegisterDTO(
    val username: String,
    val password: String,
    val name: String
)

data class ResLoginDTO(
    val token: String,
    val user: User
)

data class MessageDTO(
    val message: Message,
    val idChat: Int
)

data class FriendRequestDTO(
    var sender: User = User(),
    var receiver: User = User(),
    var requestStatus: RequestStatus = RequestStatus.REJECTED
)

data class ResUpLoadFileDTO(
    val pathToFile: String,
    val timestamp: String
)

data class NotificationRequest(
    val title: String,
    val body: String,
    val topic: String,
    val token: String,
    val imageUrl: String
)

data class UserDeviceToken(
    val token: String,
    val id: Int? = null,
    val timeStamp: String? = null,
    val user: User? = null
)

data class NotificationResponse(
    val status: Int,
    val message: String
)
