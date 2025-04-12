package com.example.echochat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.echochat.databinding.FragmentSlidingImageBinding
import com.example.echochat.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingImageFragment : Fragment() {

    private var _binding: FragmentSlidingImageBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageListAdapter: ImageListAdapter
    private val viewModel: SlidingImageVIewModel by viewModels()
    private var chatId: Int = 0
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlidingImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatId = it.getInt("chatId", 0)
            imageUrl = it.getString("imageUrl")
        }
        viewModel.getListUrlMedia(chatId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageListAdapter = ImageListAdapter()
        binding.viewPager.adapter = imageListAdapter
        observer()

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observer() {
        viewModel.listUrlState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    imageListAdapter.submitList(state.data)
                    val position = state.data.indexOf(imageUrl)
                    if(position != -1) {
                        binding.viewPager.setCurrentItem(position, false)
                    }
                }
                else -> {}
            }
        }
    }
}