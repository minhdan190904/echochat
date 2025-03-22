package com.example.echochat.repository

import android.util.Log
import  com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.model.Message
import com.example.echochat.model.NotificationRequest
import com.example.echochat.model.User
import com.example.echochat.model.UserDeviceToken
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiService
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.getFriend
import retrofit2.HttpException
import java.io.IOException

class UserRepository(private val apiService: ApiService) {
    private val friendsList = mutableListOf<User>()
    private var chatList = mutableListOf<Chat>()
    var myUser: User? = null

    init {
        myUser = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    }

    suspend fun updateUser(user: User): NetworkResource<User> {
        return try {
            Log.i("MYTAG", "User: $user")
            val userApi = apiService.updateUser(user)
            SharedPreferencesReManager.saveData(USER_SESSION, userApi.data)
            myUser = userApi.data
            NetworkResource.Success(userApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
                    500 -> "Internal Server Error. Please try again later."
                    400 -> "Email duplicated"
                    else -> "Server error: ${ex.code()}"
                }, responseCode = ex.code()
            )
        } catch (ex: IOException) {
            NetworkResource.NetworkException("Network error. Please check your connection.")
        } catch (ex: Exception) {
            NetworkResource.Error(ex.message ?: "Unexpected error")
        }
    }

    suspend fun sentFriendRequest(friendRequestDTO: FriendRequestDTO): NetworkResource<FriendRequestDTO> {
        return try {
            val friendRequestApi = apiService.sendFriendRequest(friendRequestDTO)
            NetworkResource.Success(friendRequestApi.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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

    suspend fun getUserRequests(query: String?): NetworkResource<List<FriendRequestDTO>>{
        return try {
            val friendRequestListApi = apiService.getFriendRequests(myUser?.id!!)
            if (query.isNullOrEmpty()) {
                NetworkResource.Success(friendRequestListApi.data)
            } else {
                NetworkResource.Success(friendRequestListApi.data.filter {
                    it.getFriend().name.contains(query, true)
                })
            }
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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


    suspend fun getMyFriendList(query: String?): NetworkResource<List<User>> {
        return try {
            val friendsListApi = apiService.fetchAllUser()
            val filteredFriends = friendsListApi.data.filter { it.id != myUser?.id }
            if (query.isNullOrEmpty()) {
                NetworkResource.Success(filteredFriends)
            } else {
                NetworkResource.Success(filteredFriends.filter { it.name.contains(query, true) })
            }
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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


    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        return try {
            val chatListApi = apiService.fetchAllChatByUserId(myUser?.id!!)
            val filteredChats = chatListApi.data
            chatList = filteredChats.toMutableList()
            if (query.isNullOrEmpty()) {
                NetworkResource.Success(filteredChats)
            } else {
                NetworkResource.Success(filteredChats.filter {
                    myUser?.let { user -> it.getChatTitle(user).contains(query, true) } == true
                })
            }
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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


    suspend fun getChatById(chatId: Int): NetworkResource<Chat?> {
        val chat = chatList.find { it.id == chatId }
        return if (chat != null) {
            NetworkResource.Success(chat)
        } else {
            try {
                val chatApi = apiService.fetchChatById(chatId)
                NetworkResource.Success(chatApi.data)
            } catch (ex: HttpException) {
                NetworkResource.Error(
                    message = when (ex.code()) {
                        409 -> "Email already exists"
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

    suspend fun sendMessage(chatId: Int, message: Message): NetworkResource<Chat?> {
        val chat = apiService.addMessage(chatId, message).data
        return NetworkResource.Success(chat)
    }

    suspend fun addMessage(chatId: Int, message: Message): NetworkResource<Chat?> {
        val chat = chatList.find { it.id == chatId }
        val messageRequest = apiService.sendMessage(chatId, message).data
        chat?.let {
            it.messageList.add(message)
            return NetworkResource.Success(it)
        }
        return NetworkResource.Error("Chat không tồn tại")
    }

    suspend fun sendMessageNotification(receiverId: Int, notificationRequest: NotificationRequest): NetworkResource<List<UserDeviceToken>> {
        return try {
            val response = apiService.sendNotification(receiverId, notificationRequest)
            NetworkResource.Success(response.data)
        } catch (ex: HttpException) {
            NetworkResource.Error(
                message = when (ex.code()) {
                    409 -> "Email already exists"
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