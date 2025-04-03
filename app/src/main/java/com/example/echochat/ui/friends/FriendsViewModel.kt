package com.example.echochat.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.FriendRequest
import com.example.echochat.model.dto.FriendRequestDTO
import com.example.echochat.model.dto.NotificationRequest
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.ChatRepository
import com.example.echochat.repository.FriendRequestRepository
import com.example.echochat.repository.NotificationRepository
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.UiState
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
class FriendsViewModel @Inject constructor(

    private val friendRequestRepository: FriendRequestRepository,
    private val notificationRepository: NotificationRepository,
    private val chatRepository: ChatRepository,
    private val httpClient: OkHttpClient,
    @Named("request") private val requestRequest: Request,
    private val networkMonitor: NetworkMonitor

) : ViewModel() {

    private val _usersList = MutableLiveData<List<FriendRequestDTO>>()
    val userList: LiveData<List<FriendRequestDTO>> = _usersList

    private val _usersUiState = MutableLiveData<UiState<Nothing>>()
    val usersUiState: LiveData<UiState<Nothing>> = _usersUiState

    private val _usersReceiveUiState = MutableLiveData<UiState<Nothing>>()
    val usersReceiveUiState: LiveData<UiState<Nothing>> = _usersReceiveUiState

    private val _usersSendUiState = MutableLiveData<UiState<Nothing>>()
    val usersSendUiState: LiveData<UiState<Nothing>> = _usersSendUiState

    val searchQuery = MutableLiveData<String>()

    private val _usersListReceive = MutableLiveData<List<FriendRequestDTO>>()
    val userListReceive: LiveData<List<FriendRequestDTO>> = _usersListReceive

    private val _usersListSend = MutableLiveData<List<FriendRequestDTO>>()
    val userListSend: LiveData<List<FriendRequestDTO>> = _usersListSend

    private val _friendRequestDTO = MutableLiveData<FriendRequestDTO?>()
    val friendRequestDTO: LiveData<FriendRequestDTO?> = _friendRequestDTO

    private val _chatId = MutableLiveData<Int>()
    val chatId: LiveData<Int> = _chatId

    private val _chatUiState = MutableLiveData<UiState<Nothing>>()
    val chatUiState: LiveData<UiState<Nothing>> = _chatUiState

    private val gson = Gson()
    private lateinit var webSocket: WebSocket

    init {
        networkMonitor.isNetworkAvailable.observeForever { isNetworkAvailable ->
            if (isNetworkAvailable) {
                connectWebSocket()
                getMyFriendUser()
            } else {
                if (_usersUiState.value != UiState.HasData) {
                    _usersUiState.value = UiState.NoData
                }

                if (_usersSendUiState.value != UiState.HasData) {
                    _usersSendUiState.value = UiState.NoData
                }

                if (_usersReceiveUiState.value != UiState.HasData) {
                    _usersReceiveUiState.value = UiState.NoData
                }
            }
        }
    }

    private fun connectWebSocket() {
        webSocket = httpClient.newWebSocket(requestRequest, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    val data = text.split("-")
                    if (data.contains(myUser?.id.toString())) {
                        getMyFriendUser()
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

    private fun listenerWebSocket(message: String) {
        webSocket.send(message)
    }

    fun getMyFriendUser() {
        viewModelScope.launch {
            _usersUiState.value = UiState.Loading
            _usersReceiveUiState.value = UiState.Loading
            _usersSendUiState.value = UiState.Loading
            val response = friendRequestRepository.getUserRequests(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    _usersList.value = response.data
                    _usersListReceive.value = response.data.filter {
                        it.requestStatus == FriendRequest.RequestStatus.PENDING && it.receiver.id == myUser?.id
                    }
                    _usersListSend.value = response.data.filter {
                        it.requestStatus == FriendRequest.RequestStatus.PENDING && it.sender.id == myUser?.id
                    }
                }

                is NetworkResource.Error -> {
                    response.message?.let { Log.i("Error", it) }
                }

                is NetworkResource.NetworkException -> {
                    response.message?.let { Log.i("Error", it) }
                }
            }
            if (_usersList.value.isNullOrEmpty()) {
                _usersUiState.value = UiState.NoData
            } else {
                _usersUiState.value = UiState.HasData
            }

            if (_usersListReceive.value.isNullOrEmpty()) {
                _usersReceiveUiState.value = UiState.NoData
            } else {
                _usersReceiveUiState.value = UiState.HasData
            }

            if (_usersListSend.value.isNullOrEmpty()) {
                _usersSendUiState.value = UiState.NoData
            } else {
                _usersSendUiState.value = UiState.HasData
            }
        }
    }

    fun updateFriendRequestAsync(
        friendRequestDTO: FriendRequestDTO,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val response = friendRequestRepository.sentFriendRequest(friendRequestDTO)
            when (response) {
                is NetworkResource.Success -> {
                    val message =
                        friendRequestDTO.receiver.id.toString() + "-" + friendRequestDTO.sender.id.toString() + "-" + friendRequestDTO.requestStatus
                    listenerWebSocket(message)
                    callback(true)
                    Log.i("MYTAG", message)
                }

                is NetworkResource.Error -> {
                    response.message?.let { Log.i("Error", it) }
                    callback(false)
                }

                is NetworkResource.NetworkException -> {
                    response.message?.let { Log.i("Error", it) }
                    callback(false)
                }
            }
        }
    }

    fun updateFriendRequest(friendRequestDTO: FriendRequestDTO) {
        viewModelScope.launch {
            val response = friendRequestRepository.sentFriendRequest(friendRequestDTO)
            when (response) {
                is NetworkResource.Success -> {
                    val message =
                        friendRequestDTO.receiver.id.toString() + "-" + friendRequestDTO.sender.id.toString() + "-" + friendRequestDTO.requestStatus
                    listenerWebSocket(message)
                    Log.i("MYTAG", message)
                }

                is NetworkResource.Error -> {
                    response.message?.let { Log.i("Error", it) }
                }

                is NetworkResource.NetworkException -> {
                    response.message?.let { Log.i("Error", it) }
                }
            }
        }
    }

    fun sendNotification(friend: FriendRequestDTO) {
        viewModelScope.launch {
            val response2 = notificationRepository.sendMessageNotification(
                receiverId = friend.receiver.id!!,
                notificationRequest = NotificationRequest(
                    title = friend.sender.name,
                    body = friend.sender.name + " đã gửi lời mời kết bạn",
                    topic = "",
                    token = "",
                    imageUrl = ""
                )
            )
        }
    }

    fun getChatId(user1Id: Int, user2Id: Int) {
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            val response = chatRepository.fetchChatIdByUserIds(user1Id, user2Id)
            when (response) {
                is NetworkResource.Success -> {
                    _chatId.value = response.data
                    _chatUiState.value = UiState.HasData
                    CHAT_ID = response.data
                }

                is NetworkResource.NetworkException -> {
                    _chatUiState.value = UiState.Failure(response.message)
                    CHAT_ID = -1
                }

                is NetworkResource.Error -> {
                    _chatUiState.value = UiState.Failure(response.message)
                    CHAT_ID = -1
                }
            }
        }
    }

    fun openFriendProfile(friend: FriendRequestDTO) {
        _friendRequestDTO.value = friend
    }

    fun clearFriendProfile() {
        _friendRequestDTO.value = null
    }

    fun updateFriendList(updatedList: MutableList<FriendRequestDTO>) {
        _usersList.postValue(updatedList)
    }
}