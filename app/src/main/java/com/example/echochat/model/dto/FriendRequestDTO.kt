package com.example.echochat.model.dto

import com.example.echochat.model.FriendRequest
import com.example.echochat.model.User

data class FriendRequestDTO(
    var sender: User = User(),
    var receiver: User = User(),
    var requestStatus: FriendRequest.RequestStatus = FriendRequest.RequestStatus.REJECTED
)