package com.example.echochat.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echochat.network.NetworkResource
import com.example.echochat.repository.ChatRepository
import com.example.echochat.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlidingImageVIewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {
    private val _listUrlUiState = MutableLiveData<UiState<List<String>>>(UiState.NoData)
    val listUrlState: LiveData<UiState<List<String>>> = _listUrlUiState

    fun getListUrlMedia(chatId: Int) {
        viewModelScope.launch {
            val response =chatRepository.getAllImageAndVideoUrlPath(chatId)
            when (response) {
                is NetworkResource.Success -> {
                    _listUrlUiState.value = UiState.Success(response.data)
                }
                is NetworkResource.Error -> {
                    _listUrlUiState.value = UiState.Failure(response.message)
                }

                else -> {}
            }
        }
    }
}