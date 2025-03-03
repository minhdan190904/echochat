package com.example.echochat.model

class FriendRequest(
    val id: Int? = null,
    val sender: User,
    val receiver: User,
    val requestStatus: RequestStatus = RequestStatus.PENDING,
    val timeSend: String = ""
){
    enum class RequestStatus{
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
