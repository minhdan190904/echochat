package com.example.echochat.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiClient
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.UiState
import kotlinx.coroutines.launch

class FriendsViewModel: ViewModel() {
    private var userRepository: UserRepository = UserRepository(ApiClient.apiService)

    private val _usersList = MutableLiveData<List<FriendRequestDTO>>()
    val userList: LiveData<List<FriendRequestDTO>> = _usersList

    private val _usersUiState = MutableLiveData<UiState<Nothing>>()
    val usersUiState: LiveData<UiState<Nothing>> = _usersUiState

    val searchQuery = MutableLiveData<String>()

    init {
        getMyFriendUser()
    }

    fun getMyFriendUser() {
        viewModelScope.launch {
            _usersUiState.value = UiState.Loading
            val response = userRepository.getUserRequests(searchQuery.value)
            when (response) {
                is NetworkResource.Success -> {
                    _usersList.value = response.data

                }
                is NetworkResource.Error -> {
                    response.message?.let { Log.i("Error", it) }
                }
                is NetworkResource.NetworkException ->{
                    response.message?.let { Log.i("Error", it) }
                }
            }
            _usersUiState.value = UiState.HasData
        }
    }
}