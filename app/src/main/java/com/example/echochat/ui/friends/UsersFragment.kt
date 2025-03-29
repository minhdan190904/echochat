package com.example.echochat.ui.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.example.echochat.R
import com.example.echochat.databinding.FragmentUsersBinding
import com.example.echochat.model.FriendRequest
import com.example.echochat.ui.home.HomeFragmentDirections
import com.example.echochat.util.UiState
import com.example.echochat.util.getFriend
import com.example.echochat.util.toast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.WebSocket

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding
    private val viewModel: FriendsViewModel by activityViewModels()
    private lateinit var usersListAdapter: FriendRequestListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initAdapters()
        initView()
        observeValues()
    }

    private fun initAdapters() {
        usersListAdapter = FriendRequestListAdapter(viewModel)
    }

    private fun initView() {
        binding.apply {
            usersRecyclerView.adapter = usersListAdapter
        }
    }

    private fun observeValues() {

        viewModel.userList.observe(viewLifecycleOwner) { users ->
            usersListAdapter.submitGroupedList(users)
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            viewModel.getMyFriendUser()
        }

        viewModel.usersUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.tvNoData.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = true
                }

                is UiState.NoData -> {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                is UiState.HasData -> {
                    binding.tvNoData.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                is UiState.Failure -> TODO()
                is UiState.Success -> TODO()
            }
        }
    }

}