package com.example.echochat.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.echochat.R
import com.example.echochat.databinding.FragmentChatBinding
import com.example.echochat.model.Message
import com.example.echochat.model.MessageDTO
import com.example.echochat.model.User
import com.example.echochat.network.api.ApiClient.httpClient
import com.example.echochat.network.api.ApiClient.request
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.toast
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter()
    private val chatTempList = mutableListOf<Message>()
    private val gson = Gson()
    private lateinit var webSocket: WebSocket
    private var isNotInit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getChat(CHAT_ID)
        connectWebSocket()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()
        observeValues()
        setClicks()
    }

    private fun connectWebSocket() {
        webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
//                    toast("Connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    try {
                        val messageDTO = gson.fromJson(text, MessageDTO::class.java)
                        if (messageDTO.idChat == CHAT_ID && chatTempList.lastOrNull() != messageDTO.message) {
                            chatTempList.add(messageDTO.message)
                            chatAdapter.submitList(chatTempList.toList())
                            binding.messagesRecyclerView.smoothScrollToPosition(chatTempList.size - 1)
                        }
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

    private fun initView() {
        with(binding) {
            messagesRecyclerView.setHasFixedSize(true)
            messagesRecyclerView.adapter = chatAdapter

            messagesRecyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom)
                    messagesRecyclerView.layoutManager?.smoothScrollToPosition(
                        messagesRecyclerView,
                        null,
                        chatAdapter.itemCount
                    )
            }
        }
    }

    private fun observeValues() {
        viewModel.chat.observe(viewLifecycleOwner) { chat ->
            chatAdapter.submitList(chat.messageList)
            chatTempList.clear()
            chatTempList.addAll(chat.messageList)

            if (chatTempList.isNotEmpty()) {
                binding.messagesRecyclerView.post {
                    binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                }
            }

            val receiver = if (chat.user1.id == MY_USER_ID) chat.user2 else chat.user1

            if (isNotInit) {
                val lastMessage = chat.getLastMessage()
                if (lastMessage != null) {
                    val jsonMessage = gson.toJson(chat.id?.let { MessageDTO(lastMessage, it) })
                    webSocket.send(jsonMessage)
                }
            }

            isNotInit = true

            initToolbar(receiver)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        webSocket.close(1000, "Fragment destroyed")
    }

    private fun setClicks() {
        binding.toolbar.iconBack.setOnClickListener {
            findNavController().popBackStack()
            requireActivity().finish()
        }
    }

    private fun initToolbar(receiver: User) {
        binding.toolbar.tvUserLastSeen.isVisible = true
        val lastSeenTime = receiver.lastSeen
        binding.toolbar.tvUserLastSeen.text =
            if (receiver.isOnline) getString(R.string.text_active_now)
            else getString(R.string.last_seen_time, lastSeenTime)
    }
}
