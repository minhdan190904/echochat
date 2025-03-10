package com.example.echochat.repository

import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiService
import com.example.echochat.util.USER_SESSION

class UserRepository(private val apiService: ApiService) {
    private val friendsList = mutableListOf<User>()
    private var chatList = mutableListOf<Chat>()
    private val sampleProfileImageUrlList = mutableListOf<String>()
    var myUser: User? = null

    init {
        myUser = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    }


    suspend fun getMyFriendList(query: String?): NetworkResource<List<User>> {
        val friendsListApi = apiService.fetchAllUser()
        val filteredFriends = friendsListApi.data.filter { it.id != myUser?.id }

        return if (query.isNullOrEmpty()) {
            NetworkResource.Success(filteredFriends)
        } else {
            NetworkResource.Success(filteredFriends.filter { it.name.contains(query, true) })
        }
    }


    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        val chatListApi = apiService.fetchAllChats()

        val filteredChats = chatListApi.data.filter {
            it.user1.id == myUser?.id || it.user2.id == myUser?.id
        }

        chatList = filteredChats.toMutableList()

        return if (query.isNullOrEmpty()) {
            NetworkResource.Success(filteredChats)
        } else {
            NetworkResource.Success(filteredChats.filter {
                myUser?.let { user -> it.getChatTitle(user).contains(query, true) } == true
            })
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