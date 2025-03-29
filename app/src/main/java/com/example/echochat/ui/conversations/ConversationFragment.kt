package com.example.echochat.ui.conversations

import ChatListAdapter
import FriendListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.echochat.databinding.FragmentConversationBinding
import com.example.echochat.model.dto.MessageDTO
import com.example.echochat.ui.chat.ChatActivity
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.intentActivity
import com.example.echochat.util.toast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ConversationFragment : Fragment() {
    private lateinit var binding: FragmentConversationBinding
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var chatListAdapter: ChatListAdapter
    private val gson = Gson()
    private lateinit var webSocket: WebSocket

    @Inject
    lateinit var httpClient: OkHttpClient

    @Inject
    @Named("chat")
    lateinit var requestChat: Request

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

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Fragment destroyed")
    }

    private fun connectWebSocket() {
        webSocket = httpClient.newWebSocket(requestChat, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    try {
                        val messageDTO = gson.fromJson(text, MessageDTO::class.java)
                        viewModel.updateLastMessage(messageDTO)
                    } catch (e: Exception) {
                        toast(e.message.toString())
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                lifecycleScope.launch {
                    toast(t.message.toString())
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
        }
    }

    private fun setClicks() {
        chatListAdapter.setOnClick = { chat ->
            chat.id?.let { chatId ->
                CHAT_ID = chatId
                intentActivity(ChatActivity::class.java)
            }
        }

        friendsListAdapter.setOnClick = { user ->
            val chat = chatListAdapter.currentList.find {
                val otherUser = if (it.user1.id == MY_USER_ID) it.user2 else it.user1
                otherUser.id == user.id
            }

            chat?.id?.let { chatId ->
                CHAT_ID = chatId
                intentActivity(ChatActivity::class.java)
            }
        }
    }

}
