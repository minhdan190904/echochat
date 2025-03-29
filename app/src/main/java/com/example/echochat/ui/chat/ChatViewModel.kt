package com.example.echochat.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.dto.NotificationRequest
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.ChatRepository
import com.example.echochat.repository.FileRepository
import com.example.echochat.repository.NotificationRepository
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.generateTime
import com.example.echochat.util.myUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val fileRepository: FileRepository,
    private val notificationRepository: NotificationRepository
): ViewModel() {

    private var chatId: Int? = null
    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    private val _chatUiState = MutableLiveData<UiState<Nothing>>()
    val chatUiState: LiveData<UiState<Nothing>> = _chatUiState

    val messageText = MutableLiveData("")

    private val _messageData = MutableLiveData<Message>()
    val messageData: LiveData<Message> = _messageData

    private val _imageUrl: MutableLiveData<String> = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    private val _videoUrl: MutableLiveData<String> = MutableLiveData<String>()
    val videoUrl: LiveData<String> = _videoUrl


    fun getChat(chatId: Int) {
        this.chatId = chatId
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            val response = chatRepository.getChatById(chatId)
            when (response) {
                is NetworkResource.Success -> {
                    _chat.value = response.data
                    _chatUiState.value = UiState.HasData
                }

                else -> {
                    Log.i("MYTAG", "Chat bi null")
                }
            }
        }
    }

    fun updateUserOnlineStatus(isOnline: Boolean) {
        viewModelScope.launch {
            val user = myUser!!
            user.isOnline = isOnline
            Log.i("MYTAG", user.toString())
            val response = userRepository.updateUser(user)
            when (response) {
                is NetworkResource.Success -> {
                    Log.i("MYTAG", "Update user online status success" + response.data)
                }
                else -> {
                    Log.i("MYTAG", "Update user online status failed")
                }
            }
        }
    }

    fun uploadImage(file: MultipartBody.Part){
        viewModelScope.launch {
            val response = fileRepository.uploadFile(file, "message")
            when (response) {
                is NetworkResource.Success -> {
                    _imageUrl.value = response.data.pathToFile
                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("ErrorFile", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("ErrorFile", it) }
                }
            }
        }
    }

    fun uploadVideo(file: MultipartBody.Part){
        viewModelScope.launch {
            val response = fileRepository.uploadFile(file, "message")
            when (response) {
                is NetworkResource.Success -> {
                    _videoUrl.value = response.data.pathToFile
                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("ErrorFile", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("ErrorFile", it) }
                }
            }
        }
    }

    fun sendTextMessage() {
        messageText.value?.let { msg ->
            val message = Message(
                sender = myUser,
                message = msg,
                messageType = Message.MessageType.TEXT,
                sendingTime = generateTime(),
                isSeen = false
            )

            Log.d("ChatViewModel", "Sending message: $message")

            _messageData.value = message

            messageText.value = ""

            chatId?.let {
                viewModelScope.launch {
                    val response = chatRepository.sendMessage(it, message)
                    when (response) {
                        is NetworkResource.Success -> {
//                            _chat.value = response.data
                            sendNotification(response.data!!, message, "")
                        }
                        else -> {
                            Log.e("ChatViewModel", "Lỗi gửi tin nhắn")
                        }
                    }
                }
            }

        }
    }

    fun sendVideoMessage() {
        videoUrl.value?.let { msg ->
            val message = Message(
                sender = myUser,
                message = msg,
                messageType = Message.MessageType.VIDEO,
                sendingTime = generateTime(),
                isSeen = false
            )
            _messageData.value = message
            messageText.value = ""

            chatId?.let {
                viewModelScope.launch {
                    Log.i("MYTAG", "Message: $message")
                    val response = chatRepository.sendMessage(it, message)
                    when (response) {
                        is NetworkResource.Success -> {
//                            _chat.value = response.data  // Cập nhật lại từ API nếu cần
                            sendNotification(response.data!!, message, msg)
                        }
                        else -> {
                            Log.e("ChatViewModel", "Lỗi gửi tin nhắn")
                        }
                    }
                }
            }
        }
    }

    fun sendImageMessage() {
        imageUrl.value?.let { msg ->
            val message = Message(
                sender = myUser,
                message = msg,
                messageType = Message.MessageType.IMAGE,
                sendingTime = generateTime(),
                isSeen = false
            )
            _messageData.value = message

            messageText.value = ""

            chatId?.let {
                viewModelScope.launch {
                    Log.i("MYTAG", "Message: $message")
                    val response = chatRepository.sendMessage(it, message)
                    when (response) {
                        is NetworkResource.Success -> {
                            sendNotification(response.data!!, message, msg)
                        }
                        else -> {
                            Log.e("ChatViewModel", "Lỗi gửi tin nhắn")
                        }
                    }
                }
            }

        }
    }

    suspend fun sendNotification(chat: Chat, message: Message, imageUrl: String) {
        if(message.messageType == Message.MessageType.IMAGE){
            message.message = "Đã gửi 1 ảnh"
        } else if(message.messageType == Message.MessageType.VIDEO){
            message.message = "Đã gửi 1 video"
        }
        val response2 = notificationRepository.sendMessageNotification(
            receiverId = chat.getOtherUser(myUser!!).id!!,
            notificationRequest = NotificationRequest(
                title = message.sender!!.name,
                body = message.message,
                topic = "",
                token = "",
                imageUrl = imageUrl
            )
        )
    }
}