package com.example.echochat.repository;

import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ChatApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.myUser
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi
) {
    private var chatList = mutableListOf<Chat>()

    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        return try {
            val chatListApi = chatApi.fetchAllChatByUserId(myUser?.id!!)
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
                    404 -> "Error fetching chats"
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

    suspend fun fetchChatIdByUserIds(user1Id: Int, user2Id: Int): NetworkResource<Int> {
        return try {
            val chatIdApi = chatApi.fetchChatIdByUserIds(user1Id, user2Id)
            NetworkResource.Success(chatIdApi.data)
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
                val chatApi = chatApi.fetchChatById(chatId)
                NetworkResource.Success(chatApi.data)
            } catch (ex: HttpException) {
                NetworkResource.Error(
                    message = when (ex.code()) {
                        404 -> "Error fetching chat"
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
        val chat = chatApi.addMessage(chatId, message).data
        return NetworkResource.Success(chat)
    }

    suspend fun addMessage(chatId: Int, message: Message): NetworkResource<Chat?> {
        val chat = chatList.find { it.id == chatId }
        val messageRequest = chatApi.sendMessage(chatId, message).data
        chat?.let {
            it.messageList.add(message)
            return NetworkResource.Success(it)
        }
        return NetworkResource.Error("Chat không tồn tại")
    }
}
