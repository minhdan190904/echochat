package com.example.echochat.ui.friends

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPageFriendsAdapter (
    fragment: FragmentManager,
    lifecycle: Lifecycle
    ): FragmentStateAdapter(fragment, lifecycle) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> UsersFragment()
                1 -> ReceivedFriendRequestsFragment()
                else -> SentFriendRequestsFragment()
            }
        }

    }