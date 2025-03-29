package com.example.echochat.ui.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.echochat.R
import com.example.echochat.databinding.FragmentFriendsBinding
import com.example.echochat.model.FriendRequest
import com.example.echochat.ui.home.HomeFragmentDirections
import com.example.echochat.util.getFriend
import com.example.echochat.util.toast
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private val viewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        viewModel.friendRequestDTO.observe(viewLifecycleOwner) { friendRequestDTO ->
            if(friendRequestDTO != null){
                if(friendRequestDTO.requestStatus != FriendRequest.RequestStatus.ACCEPTED){
                    toast("Các bạn chưa là bạn bè")
                } else {
                    friendRequestDTO.getFriend().id?.let {
                        requireActivity().findNavController(R.id.nav_host_fragment_main).navigate(
                            HomeFragmentDirections.actionHomeFragmentToFriendProfileFragment(it)
                        )
                        viewModel.clearFriendProfile()
                    }
                }
            }
        }

        binding.viewPager2.adapter = FragmentPageFriendsAdapter(childFragmentManager, lifecycle)

        binding.apply {
            tabIndicator.addTab(tabIndicator.newTab().setText("USERS"))
            tabIndicator.addTab(tabIndicator.newTab().setText("RECEIVED"))
            tabIndicator.addTab(tabIndicator.newTab().setText("SENT"))
        }

        binding.tabIndicator.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab != null){
                    binding.viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabIndicator.selectTab(binding.tabIndicator.getTabAt(position))
            }
        })
    }
}