package com.example.echochat.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.echochat.R
import com.example.echochat.databinding.FragmentFriendProfileBinding

class FriendProfileFragment : Fragment() {

    private lateinit var binding: FragmentFriendProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

}