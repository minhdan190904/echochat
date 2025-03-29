package com.example.echochat.ui.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.echochat.databinding.FragmentUsersBinding
import com.example.echochat.util.UiState
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.WebSocket

@AndroidEntryPoint
class SentFriendRequestsFragment : Fragment() {

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
        viewModel.userListSend.observe(viewLifecycleOwner) { users ->
            usersListAdapter.submitGroupedList(users)
        }

        viewModel.usersSendUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                    binding.tvNoData.visibility = View.GONE
                }
                is UiState.NoData -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.tvNoData.visibility = View.VISIBLE
                }
                is UiState.HasData -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.tvNoData.visibility = View.GONE
                }

                is UiState.Failure -> TODO()
                is UiState.Success -> TODO()
            }
        }

    }

}