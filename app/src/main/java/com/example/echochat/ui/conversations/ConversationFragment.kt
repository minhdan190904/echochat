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
import com.example.echochat.databinding.FragmentConversationBinding
import com.example.echochat.ui.chat.ChatActivity
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.intentActivity
import com.example.echochat.util.myFriend
import com.example.echochat.util.myUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConversationFragment : Fragment() {
    private lateinit var binding: FragmentConversationBinding
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var friendsListAdapter: FriendListAdapter
    private lateinit var chatListAdapter: ChatListAdapter

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

    override fun onStart() {
        super.onStart()
        viewModel.getMyConversations()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initAdapters()
        initView()
        observeValues()
        setClicks()
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
                myFriend = chat.getOtherUser(myUser!!)
                intentActivity(ChatActivity::class.java)
            }
        }

        friendsListAdapter.setOnClick = { user ->
            val chat = chatListAdapter.currentList.find {
                val otherUser = if (it.user1.id == myUser?.id) it.user2 else it.user1
                myFriend = otherUser
                Log.i("MYTAG12", otherUser.toString())
                otherUser.id == user.id
            }

            chat?.id?.let { chatId ->
                CHAT_ID = chatId
                intentActivity(ChatActivity::class.java)
            }
        }
    }

}
