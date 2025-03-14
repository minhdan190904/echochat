package com.example.echochat.repository

import  com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiService
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.getDisplayName
import retrofit2.HttpException
import java.io.IOException

class UserRepository(private val apiService: ApiService) {
    private val friendsList = mutableListOf<User>()
    private var chatList = mutableListOf<Chat>()
    var myUser: User? = null

    init {
        myUser = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    }

    suspend fun getUserRequests(query: String?): NetworkResource<List<FriendRequestDTO>>{
        return try {
            val friendRequestListApi = apiService.getFriendRequests(myUser?.id!!)
            if (query.isNullOrEmpty()) {
                NetworkResource.Success(friendRequestListApi.data)
            } else {
                NetworkResource.Success(friendRequestListApi.data.filter {
                    it.getDisplayName().contains(query, true)
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
            val chatListApi = apiService.fetchAllChats()
            val filteredChats = chatListApi.data.filter {
                it.user1.id == myUser?.id || it.user2.id == myUser?.id
            }
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
//        val chatListApi = apiService.fetchAllChats()
        return NetworkResource.Success(apiService.fetchChatById(chatId).data)
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

}