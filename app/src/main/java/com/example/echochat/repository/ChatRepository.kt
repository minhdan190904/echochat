package com.example.echochat.repository;

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.echochat.db.dao.ChatDao
import com.example.echochat.db.dao.MessageDao
import com.example.echochat.db.dao.UserDao
import com.example.echochat.db.entity.toEntity
import com.example.echochat.db.entity.toModel
import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ChatApi
import com.example.echochat.util.MESSAGE_ID
import com.example.echochat.util.handleNetworkCall
import com.example.echochat.util.myUser
import com.example.echochat.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context
) {
    private var chatList = mutableListOf<Chat>()

    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        Log.i("ChatRepository", networkMonitor.isNetworkConnected().toString())
        return if (networkMonitor.isNetworkConnected()) {
            handleNetworkCall(
                call = {
                    val chatListApi = chatApi.fetchAllChatByUserId(myUser?.id!!)
                    val chats = chatListApi.data
                    chatList = chats.toMutableList()
                    userDao.insertUsers(chats.flatMap { listOf(it.user1, it.user2) }
                        .map { it.toEntity() })
                    chatDao.insertChats(chats.map { it.toEntity() })
                    chats.forEach { chat ->
                        val messages = chat.messageList.map { it.toEntity(chat.id!!) }
                        messageDao.insertMessages(messages.map { it })
                    }
                    if (query.isNullOrEmpty()) {
                        chats
                    } else {
                        chats.filter {
                            myUser?.let { user ->
                                it.getChatTitle(user).contains(query, true)
                            } == true
                        }
                    }
                },
                customErrorMessages = mapOf(400 to "Error fetching chats")
            )
        } else {
            val chats = chatDao.getChatsForUser(myUser?.id!!).map { entities ->
                entities.map { entity ->
                    val user1 = userDao.getUserById(entity.user1Id)?.toModel()
                    val user2 = userDao.getUserById(entity.user2Id)?.toModel()
                    val messages = messageDao.getMessagesForChat(entity.id).map {
                        it.toList().map { msg ->
                            msg.toModel(
                                if (msg.senderId == user1?.id) user1 else user2
                            )
                        }
                    }
                    Chat(
                        user1 = user1 ?: User(),
                        user2 = user2 ?: User(),
                        id = entity.id,
                        timeCreated = entity.timeCreated,
                        messageList = messages.first().toMutableList()
                    )
                }
            }.first()
            chatList = chats.toMutableList()
            NetworkResource.Success(chats)
        }
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

    private fun scheduleMessageSync(messageId: Int): UUID {
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(workDataOf(MESSAGE_ID to messageId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                androidx.work.BackoffPolicy.LINEAR,
                5,
                java.util.concurrent.TimeUnit.SECONDS
            )
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
        return workRequest.id
    }


    suspend fun sendMessage(chatId: Int, message: Message): Pair<NetworkResource<Chat?>, UUID?> {
        val messageEntity = message.toEntity(chatId).copy(
            isUploading = true
        )
        val generatedId = messageDao.insertMessage(messageEntity).toInt()
        Log.i("ChatRepository", "Generated ID for message: $generatedId")

        if (networkMonitor.isNetworkConnected()) {
            return try {
                val response = chatApi.addMessage(chatId, message.copy(id = null))
                val chat = response.data

                chat?.let {
                    // Cập nhật danh sách local
                    chatList.removeIf { it.id == chatId }
                    chatList.add(it)

                    // Cập nhật DB
                    messageDao.deleteMessage(generatedId)
                    messageDao.insertMessages(it.messageList.map { msg -> msg.toEntity(chatId) })
                    chatDao.insertChats(listOf(it.toEntity()))
                    userDao.insertUsers(listOf(it.user1, it.user2).map { user -> user.toEntity() })
                }

                Pair(NetworkResource.Success(chat), null)
            } catch (e: Exception) {
                val workId = scheduleMessageSync(generatedId)
                val fallbackChat = chatList.find { it.id == chatId }?.apply {
                    messageList.add(
                        message.copy(
                            id = generatedId,
                            isUploading = true,
                            idLoading = messageEntity.idLoading
                        )
                    )
                }
                Pair(NetworkResource.Success(fallbackChat), workId)
            }
        } else {
            val workId = scheduleMessageSync(generatedId)
            val fallbackChat = chatList.find { it.id == chatId }?.apply {
                messageList.add(
                    message.copy(
                        id = generatedId,
                        isUploading = true,
                        idLoading = messageEntity.idLoading
                    )
                )
            }
            return Pair(NetworkResource.Success(fallbackChat), workId)
        }
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
        val listUrl =
            messageList?.filter { it.messageType == Message.MessageType.IMAGE || it.messageType == Message.MessageType.VIDEO }
                ?.mapNotNull { it.message }?.toList()
        if (listUrl != null) {
            return NetworkResource.Success(listUrl)
        }
        return NetworkResource.Error("Error fetching image and video URLs")
    }
}