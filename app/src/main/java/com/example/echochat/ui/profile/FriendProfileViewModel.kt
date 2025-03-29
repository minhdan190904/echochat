package com.example.echochat.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.UserRepository
import com.example.echochat.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _friendUser = MutableLiveData<User>()
    val friendUser: LiveData<User> = _friendUser

    private val _friendUiState = MutableLiveData<UiState<Nothing>>()
    val friendUiState: LiveData<UiState<Nothing>> = _friendUiState

    fun getFriendUser(userId: Int) {
        viewModelScope.launch {
            _friendUiState.value = UiState.Loading
            val response = userRepository.fetchUserById(userId)
            when (response) {
                is NetworkResource.Success -> {
                    _friendUser.value = response.data
                    _friendUiState.value = UiState.HasData
                }

                is NetworkResource.NetworkException -> {
                    _friendUiState.value = UiState.Failure(response.message)
                }

                is NetworkResource.Error -> {
                    _friendUiState.value = UiState.Failure(response.message)
                }
            }
        }
    }
}