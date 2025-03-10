package com.example.echochat.ui.conversations

import ChatListAdapter
import FriendListAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.echochat.util.UiState
import com.example.echochat.databinding.FragmentConversationBinding
import com.example.echochat.model.MessageDTO
import com.example.echochat.network.api.ApiClient.httpClient
import com.example.echochat.network.api.ApiClient.request
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ConversationFragment : Fragment() {
    private lateinit var binding: FragmentConversationBinding
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var chatListAdapter: ChatListAdapter
    private val gson = Gson()
    private lateinit var webSocket: WebSocket

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversationBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initAdapters()
        initView()
        observeValues()
        setClicks()
        connectWebSocket()
    }

    private fun connectWebSocket() {
        webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
                    toast("Connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    try {
                        viewModel.getMyConversations()
                        viewModel.getMyFriendList()
                    } catch (e: Exception) {
                        Log.e("WebSocket", "JSON Parse Error: ${e.message}")
                    }
                }
            }


            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                lifecycleScope.launch {
                    toast(t.message.toString())
                    Log.i("WebSocket", "Error: ${t.message}")
                }
            }
        })
    }

    private fun initAdapters() {
        friendsListAdapter = FriendListAdapter()
        chatListAdapter = ChatListAdapter()
    }

    private fun initView() {
        binding.apply {
            friendsRecyclerView.adapter = friendsListAdapter
            chatsRecyclerView.adapter = chatListAdapter
        }
    }

    private fun observeValues() {
        viewModel.friendsList.observe(viewLifecycleOwner) { friends ->
            friendsListAdapter.submitList(friends)
        }
        viewModel.chatList.observe(viewLifecycleOwner) { chats ->
            chatListAdapter.submitList(chats)
        }
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            viewModel.getMyConversations()
            viewModel.getMyFriendList()
        }

        viewModel.friendsUiState.observe(viewLifecycleOwner){
           if(it is UiState.Loading){
               binding.progressBar.show()
           } else binding.progressBar.hide()
        }

    }

    private fun setClicks() {
        chatListAdapter.setOnClick = { chat ->
            Log.i("MYTAG", chat?.id.toString())
            chat.id?.let { chatId ->
                Log.i("MYTAG", chat?.id.toString())
                findNavController().navigate(
                    ConversationFragmentDirections.actionConversationFragmentToChatFragment(chatId)
                )
            }
        }

        friendsListAdapter.setOnClick = { user ->
            val chat = chatListAdapter.currentList.find {
                val otherUser = if (it.user1.id == MY_USER_ID) it.user2 else it.user1
                otherUser.id == user.id
            }

            chat?.id?.let { chatId ->
                findNavController().navigate(
                    ConversationFragmentDirections.actionConversationFragmentToChatFragment(chatId)
                )
            }
        }
    }

}
