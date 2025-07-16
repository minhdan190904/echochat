package com.example.echochat.ui.conversations

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequest
import com.example.echochat.model.User
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.ChatRepository
import com.example.echochat.util.CHAT_REQUEST
import com.example.echochat.util.NORMAL_CLOSURE_STATUS
import com.example.echochat.util.REQUEST_REQUEST
import com.example.echochat.util.RETRY_TIME_WEB_SOCKET
import com.example.echochat.util.myUser
import com.example.echochat.util.toast
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ConversationViewModel @Inject constructor(

    private val chatRepository: ChatRepository,
    private val httpClient: OkHttpClient,
    @Named(REQUEST_REQUEST) private val requestRequest: Request,
    @Named(CHAT_REQUEST) private  val requestChat: Request,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val applicationContext: Context

): ViewModel() {

    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> = _chatList

    private val _friendsList = MutableLiveData<List<User>>()
    val friendsList: LiveData<List<User>> = _friendsList

    private val _chatUiState = MutableLiveData<UiState<Nothing>>()
    val chatUiState: LiveData<UiState<Nothing>> = _chatUiState

    private val _friendsUiState = MutableLiveData<UiState<Nothing>>()

    val searchQuery = MutableLiveData<String>()

    private var webSocketRequest: WebSocket? = null
    private var webSocketChat: WebSocket? = null

    private var isWebSocketRequestConnected: Boolean = false
    private var isWebSocketRequestConnecting: Boolean = false

    private var isWebSocketChatConnected: Boolean = false
    private var isWebSocketChatConnecting: Boolean = false

    init {
        networkMonitor.isNetworkAvailable.observeForever { isAvailable ->
            if (isAvailable) {
                getMyConversations()
                connectWebSocketRequest()
                connectWebSocketChat()
            } else {
                if(_chatUiState.value != UiState.HasData){
                    _chatUiState.value = UiState.NoData
                    _friendsUiState.value = UiState.NoData
                }
            }
        }
    }

    private fun connectWebSocketRequest(){
        if (isWebSocketRequestConnected || isWebSocketRequestConnecting) {
            return
        }
        disconnectWebSocketRequest()
        isWebSocketRequestConnecting = true

        webSocketRequest = httpClient.newWebSocket(requestRequest, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                viewModelScope.launch {
                    isWebSocketRequestConnected = true
                    isWebSocketRequestConnecting = false
                    applicationContext.toast("WebSocket request connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    val data = text.split("-")
                    if(data.contains(myUser?.id.toString()) &&
                        data.contains(FriendRequest.RequestStatus.ACCEPTED.toString())){
                        getMyConversations()
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                viewModelScope.launch {
                    isWebSocketRequestConnected = false
                    isWebSocketRequestConnecting = false
                    if (code != NORMAL_CLOSURE_STATUS && networkMonitor.isNetworkConnected()) {
                        connectWebSocketRequest()
                    } else {
                        applicationContext.toast("WebSocket request closed: $reason")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                viewModelScope.launch {
                    isWebSocketRequestConnected = false
                    isWebSocketRequestConnecting = false
                    if(networkMonitor.isNetworkConnected()) {
                        delay(RETRY_TIME_WEB_SOCKET)
                        if(isActive){
                            connectWebSocketRequest()
                        }
                    } else {
                        applicationContext.toast("WebSocket request failed: ${t.message}")
                    }
                }
            }
        })
    }

    private fun connectWebSocketChat() {
        if (isWebSocketChatConnected || isWebSocketChatConnecting) return
        disconnectWebSocketChat()
        isWebSocketChatConnecting = true

        webSocketChat = httpClient.newWebSocket(requestChat, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                viewModelScope.launch {
                    isWebSocketChatConnected = true
                    isWebSocketChatConnecting = false
                    applicationContext.toast("WebSocket chat connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    try {
                        getMyConversations()
                        Log.i("WEBSOCKET1", "Load conversation")
                    } catch (e: Exception) {
                        Log.i("WEBSOCKET1", "Load conversation false")
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                viewModelScope.launch {
                    isWebSocketChatConnected = false
                    isWebSocketChatConnecting = false
                    if (code != NORMAL_CLOSURE_STATUS && networkMonitor.isNetworkConnected()) {
                        connectWebSocketChat()
                    } else {
                        applicationContext.toast("WebSocket chat closed: $reason")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                viewModelScope.launch {
                    isWebSocketChatConnected = false
                    isWebSocketChatConnecting = false
                    if(networkMonitor.isNetworkConnected()) {
                        delay(RETRY_TIME_WEB_SOCKET)
                        if(isActive){
                            connectWebSocketChat()
                        }
                    } else {
                        applicationContext.toast("WebSocket chat failed: ${t.message}")
                    }
                }
            }
        })
    }

    fun getMyConversations() {
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            _friendsUiState.value = UiState.Loading
            val response = chatRepository.getMyConversations(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    Log.i("MYTAG", response.data.toString())
                    _chatList.value = response.data.sortedWith(
                        compareByDescending { chat ->
                            chat.getLastMessage()?.sendingTime ?: chat.timeCreated
                        }
                    )
                    _friendsList.value = response.data.mapNotNull { myUser?.let { it1 ->
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

    private fun disconnectWebSocketRequest() {
        webSocketRequest?.close(1000, "ViewModel cleared")
        webSocketRequest = null
        isWebSocketRequestConnected = false
        isWebSocketRequestConnecting = false
    }

    private fun disconnectWebSocketChat() {
        webSocketChat?.close(1000, "ViewModel cleared")
        webSocketChat = null
        isWebSocketChatConnected = false
        isWebSocketChatConnecting = false
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocketRequest()
        disconnectWebSocketChat()
    }
}