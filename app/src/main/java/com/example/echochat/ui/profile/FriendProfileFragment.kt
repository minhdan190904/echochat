package com.example.echochat.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.echochat.databinding.FragmentFriendProfileBinding
import com.example.echochat.ui.chat.ChatActivity
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.intentActivity
import com.example.echochat.util.myFriend
import com.example.echochat.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendProfileFragment : Fragment() {

    private lateinit var binding: FragmentFriendProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.user = myFriend

        binding.btnChat.setOnClickListener {
            if(CHAT_ID != -1){
                intentActivity(ChatActivity::class.java)
            } else {
                toast("Chưa xử lý được")
            }
        }
    }
}