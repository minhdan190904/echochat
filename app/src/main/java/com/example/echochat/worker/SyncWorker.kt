package com.example.echochat.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.echochat.db.dao.MessageDao
import com.example.echochat.db.dao.UserDao
import com.example.echochat.db.entity.toEntity
import com.example.echochat.db.entity.toModel
import com.example.echochat.db.entity.toModelNullId
import com.example.echochat.network.api.ChatApi
import com.example.echochat.util.MESSAGE_ID
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val messageDao: MessageDao,
    private val userDao: UserDao,
    private val apiService: ChatApi
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val messageId = inputData.getInt(MESSAGE_ID, -1)
        if (messageId == -1) {
            Log.e("SyncWorker", "Invalid messageId")
            return Result.failure()
        }

        val message = messageDao.getMessageById(messageId) ?: run {
            Log.e("SyncWorker", "Message not found for ID: $messageId")
            return Result.failure()
        }

        val sender = userDao.getUserById(message.senderId)
            ?: run {
                Log.e("SyncWorker", "Sender not found for ID: ${message.senderId}")
                return Result.failure()
            }

        return try {
            val messageToBackend = message.toModelNullId(sender.toModel())
            Log.i("SyncWorker", "Chat ID: ${message.chatId}, Message ID: $messageId, Message: ${messageToBackend}")
            val response = apiService.sendMessageSync(message.chatId, messageToBackend)
            val serverMessage = response.data.toEntity(message.chatId)
            serverMessage.let {
                messageDao.deleteMessage(messageId)
                messageDao.insertMessage(it.copy(isUploading = false, idLoading = null))
                Log.i("SyncWorker", "Successfully synced message $messageId")
            }
            val gson = Gson()
            val messageJson = gson.toJson(messageToBackend)

            val output = workDataOf(
                "messageJson" to messageJson
            )

            Result.success(output)
        } catch (e: Exception) {
            Log.e("SyncWorker", "Failed to sync message $messageId: ${e.message}")
            Result.retry()
        }
    }
}