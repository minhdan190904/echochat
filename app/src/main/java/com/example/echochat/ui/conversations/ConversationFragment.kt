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
import androidx.navigation.fragment.findNavController
import com.example.echochat.util.UiState
import com.example.echochat.databinding.FragmentConversationBinding
import com.example.echochat.model.NormalChat
import com.example.echochat.util.hide
import com.example.echochat.util.show

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
                it is NormalChat && it.receiver.id == user.id
            }
            (chat as? NormalChat)?.id?.let { chatId ->
                findNavController().navigate(
                    ConversationFragmentDirections.actionConversationFragmentToChatFragment(chatId)
                )
            }
        }
    }

}
