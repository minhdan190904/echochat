package com.example.echochat.ui.conversations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.MessageDTO
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.UserRepository
import kotlinx.coroutines.launch

class ConversationViewModel: ViewModel() {

    private var userRepository: UserRepository = UserRepository(ApiClient.apiService)

    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> = _chatList

    private val _friendsList = MutableLiveData<List<User>>()
    val friendsList: LiveData<List<User>> = _friendsList

    private val _chatUiState = MutableLiveData<UiState<Nothing>>()
    val chatUiState: LiveData<UiState<Nothing>> = _chatUiState

    private val _friendsUiState = MutableLiveData<UiState<Nothing>>()
    val friendsUiState: LiveData<UiState<Nothing>> = _friendsUiState

    val searchQuery = MutableLiveData<String>()


    init {
        getMyConversations()
        getMyFriendList()
    }

    fun getMyFriendList() {
        viewModelScope.launch {
            _friendsUiState.value = UiState.Loading
            val response = userRepository.getMyFriendList(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    _friendsList.value = response.data
                    _friendsUiState.value = UiState.HasData

                }

                else -> {}
            }
        }
    }


    fun getMyConversations() {
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            val response = userRepository.getMyConversations(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    _chatList.value = response.data
                    _chatUiState.value = UiState.HasData

                }
                is NetworkResource.Error -> TODO()
                is NetworkResource.NetworkException -> TODO()
            }
        }
    }
}