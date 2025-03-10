package com.example.echochat.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.generateTime
import kotlinx.coroutines.launch


class ChatViewModel (
    private val userRepository: UserRepository = UserRepository(ApiClient.apiService)
) : ViewModel() {

    private var chatId: Int? = null
    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat> = _chat

    private val _chatUiState = MutableLiveData<UiState<Nothing>>()
    val chatUiState: LiveData<UiState<Nothing>> = _chatUiState

    //Represents user's message
    val messageText = MutableLiveData("")


    fun getChat(chatId: Int) {
        this.chatId = chatId
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            val response = userRepository.getChatById(chatId)
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

    fun onClickSendMessage() {
        messageText.value?.let { msg ->
            val message = Message(userRepository.myUser, msg, generateTime())
            chatId?.let {
                viewModelScope.launch {
                    val response = userRepository.sendMessage(it, message)
                    when (response) {
                        is NetworkResource.Success -> {
                            _chat.value = response.data
                            messageText.value = "" //Remove message in order to write new message
                        }

                        else -> {}
                    }
                }
            }
        }
    }


}