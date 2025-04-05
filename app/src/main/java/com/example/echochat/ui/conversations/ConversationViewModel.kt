package com.example.echochat.ui.conversations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.util.UiState
import com.example.echochat.model.Chat
import com.example.echochat.model.FriendRequest
import com.example.echochat.model.User
import com.example.echochat.model.dto.MessageDTO
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.ChatRepository
import com.example.echochat.util.myUser
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
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
    @Named("request") private val requestRequest: Request,
    @Named("chat") private  val requestChat: Request,
    private val networkMonitor: NetworkMonitor

): ViewModel() {

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
    private var webSocket: WebSocket? = null
    private var webSocket2: WebSocket? = null

    init {
        networkMonitor.isNetworkAvailable.observeForever { isAvailable ->
            if (isAvailable) {
                getMyConversations()
                connectWebsocket()
                connectWebSocket2()
            } else {
                if(_chatUiState.value != UiState.HasData){
                    _chatUiState.value = UiState.NoData
                    _friendsUiState.value = UiState.NoData
                }
            }
        }
    }

    private fun closeWebSocket1() {
        webSocket?.close(1000, "ViewModel cleared")
        webSocket = null
    }

    private fun closeWebSocket2() {
        webSocket2?.close(1000, "ViewModel cleared")
        webSocket2 = null
    }

    private fun connectWebsocket(){
        closeWebSocket1()
        webSocket = httpClient.newWebSocket(requestRequest, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
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
            val response = chatRepository.getMyConversations(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    Log.i("MYTAG", response.data.toString())
                    _chatList.value = response.data.sortedWith(
                        compareByDescending<Chat> { chat ->
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

    private fun connectWebSocket2() {
        closeWebSocket2()
        webSocket2 = httpClient.newWebSocket(requestChat, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                viewModelScope.launch {
                    Log.i("WEBSOCKET1", "Connect websocket")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    try {
                        val messageDTO = gson.fromJson(text, MessageDTO::class.java)
                        getMyConversations()
                        Log.i("WEBSOCKET1", "Load conversation")
                    } catch (e: Exception) {
                        Log.i("WEBSOCKET1", "Load conversation false")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                viewModelScope.launch {
                    Log.i("WEBSOCKET1", "Close connection")
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        closeWebSocket1()
        closeWebSocket2()
    }
}