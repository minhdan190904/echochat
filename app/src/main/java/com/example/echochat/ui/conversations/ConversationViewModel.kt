package com.example.echochat.ui.conversations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequest
import com.example.echochat.model.MessageDTO
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.network.api.ApiClient.httpClient
import com.example.echochat.network.api.ApiClient.request_request
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.MY_USER_ID
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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

    private val gson = Gson()
    private lateinit var webSocket: WebSocket


    init {
        getMyConversations()
        webSocket = httpClient.newWebSocket(request_request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    val data = text.split("-")
                    if(data.contains(MY_USER_ID.toString()) &&
                        data.contains(FriendRequest.RequestStatus.ACCEPTED.toString())){
                        getMyConversations()
                    }
                }
            }


            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                viewModelScope.launch {
                    Log.i("MYTAG", t.message.toString())
                }
            }
        })
    }

    fun getMyConversations() {
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            _friendsUiState.value = UiState.Loading
            val response = userRepository.getMyConversations(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    _chatList.value = response.data.sortedBy { it.getLastMessage()?.id }.reversed()
                    _friendsList.value = response.data.mapNotNull { userRepository.myUser?.let { it1 ->
                        it.getOtherUser(
                            it1
                        )
                    } }
                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("Error", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("Error", it) }
                }
            }
            if(_chatList.value.isNullOrEmpty()) {
                _chatUiState.value = UiState.NoData
                _friendsUiState.value = UiState.NoData
            } else {
                _chatUiState.value = UiState.HasData
                _friendsUiState.value = UiState.HasData
            }
        }
    }

    fun updateLastMessage(messageDTO: MessageDTO) {
        val updatedList = _chatList.value?.toMutableList() ?: return
        val index = updatedList.indexOfFirst { it.id == messageDTO.idChat }
        if (index != -1) {
            getMyConversations()
        }
    }

}