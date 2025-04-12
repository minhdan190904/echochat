package com.example.echochat.repository;

import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ChatApi
import com.example.echochat.util.handleNetworkCall
import com.example.echochat.util.myUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi
) {
    private var chatList = mutableListOf<Chat>()

    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        return handleNetworkCall(
            call = {
                val chatListApi = chatApi.fetchAllChatByUserId(myUser?.id!!)
                val filteredChats = chatListApi.data
                chatList = filteredChats.toMutableList()
                if (query.isNullOrEmpty()) {
                    filteredChats
                } else {
                    filteredChats.filter {
                        myUser?.let { user -> it.getChatTitle(user).contains(query, true) } == true
                    }
                }
            },
            customErrorMessages = mapOf(
                400 to "Error fetching chats"
            )
        )
    }

    suspend fun fetchChatIdByUserIds(user1Id: Int, user2Id: Int): NetworkResource<Int> {
        val chat = chatList.find { user2Id == it.getOtherUser(myUser!!).id }
        return if (chat != null) {
            NetworkResource.Success(chat.id!!)
        } else {
            return handleNetworkCall(
                call = { chatApi.fetchChatIdByUserIds(user1Id, user2Id).data },
                customErrorMessages = mapOf(
                    400 to "Error fetching chat ID"
                )
            )
        }
    }

    suspend fun getChatById(chatId: Int, isFetchOnline: Boolean): NetworkResource<Chat?> {
        val chat = chatList.find { it.id == chatId }
        return if (chat != null && !isFetchOnline) {
            NetworkResource.Success(chat)
        } else {
            return handleNetworkCall(
                call = { chatApi.fetchChatById(chatId).data },
                customErrorMessages = mapOf(
                    400 to "Error fetching chat"
                )
            )
        }
    }

    suspend fun sendMessage(chatId: Int, message: Message): NetworkResource<Chat?> {
        return handleNetworkCall(
            call = { chatApi.addMessage(chatId, message).data },
            customErrorMessages = mapOf(
                400 to "Error sending message"
            )
        )
    }

    suspend fun updateSeenLastMessage(chatId: Int): NetworkResource<Chat> {
        return handleNetworkCall(
            call = { chatApi.updateSeenLastMessage(chatId).data },
            customErrorMessages = mapOf(
                400 to "Error updating last seen message"
            )
        )
    }

    suspend fun getAllImageAndVideoUrlPath(chatId: Int): NetworkResource<List<String>> {
        val messageList = chatList.find { it.id == chatId }?.messageList
        val listUrl = messageList?.filter { it.messageType == Message.MessageType.IMAGE || it.messageType == Message.MessageType.VIDEO }
            ?.mapNotNull { it.message }?.toList()
        if(listUrl != null) {
            return NetworkResource.Success(listUrl)
        }
        return NetworkResource.Error("Error fetching image and video URLs")
    }
}
