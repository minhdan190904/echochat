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
import com.example.echochat.model.Chat
import com.example.echochat.model.GroupChat
import com.example.echochat.model.Message
import com.example.echochat.model.NormalChat
import com.example.echochat.util.toast
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private val chatAdapter = ChatAdapter()

    private val chatTempList = mutableListOf<Message>()

    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getChat(args.chatId)
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
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url("ws://135b-2001-ee0-1ac0-2f6d-7caf-f89e-c483-be13.ngrok-free.app/chat").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
                    toast("Connected")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    toast("Message received: $text")
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

            //This helps to scroll to the last message when keyboard is opened
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
            chatTempList.lastOrNull()?.message?.let { webSocket.send(it) }
            binding.messagesRecyclerView.scrollToPosition(chat.messageList.size - 1)
            initToolbar(chat)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocket.close(1000, "Fragment destroyed")
    }



    private fun setClicks() {
        binding.toolbar.iconBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initToolbar(chat: Chat) {

        when (chat) {
            is GroupChat -> {
                binding.toolbar.tvUserLastSeen.isVisible = false
            }
            is NormalChat -> {
                binding.toolbar.tvUserLastSeen.isVisible = true
                val lastSeenTime = chat.receiver.lastSeen
                binding.toolbar.tvUserLastSeen.text =
                    if (chat.receiver.isOnline) getString(R.string.text_active_now)
                    else getString(R.string.last_seen_time, lastSeenTime)
            }
        }

    }


}