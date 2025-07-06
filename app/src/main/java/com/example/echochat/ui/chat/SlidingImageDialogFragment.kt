package com.example.echochat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.echochat.databinding.SlidingImageDialogBinding
import com.example.echochat.network.Downloader
import com.example.echochat.util.BASE_URL_GET_IMAGE
import com.example.echochat.util.UiState
import com.example.echochat.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SlidingImageDialogFragment : DialogFragment() {

    private lateinit var binding: SlidingImageDialogBinding
    private val viewModel: SlidingImageVIewModel by viewModels()

    private var chatId: Int = 0
    private var imageUrl: String? = null

    @Inject
    lateinit var downloader: Downloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatId = it.getInt("chatId", 0)
            imageUrl = it.getString("imageUrl")
        }
        viewModel.getListUrlMedia(chatId)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SlidingImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImageListAdapter()
        binding.viewPager.adapter = adapter

        binding.imgBack.setOnClickListener {
            dismiss()
        }

        binding.imgDownload.setOnClickListener {
            dowloadFile()
        }

        viewModel.listUrlState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.submitList(state.data)
                    val position = state.data.indexOf(imageUrl)
                    if (position != -1) {
                        binding.viewPager.setCurrentItem(position, false)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun dowloadFile() {
        try {
            imageUrl?.let { url ->
                downloader.downloadFile(BASE_URL_GET_IMAGE + url)
            }
        } catch (e: Exception) {
            toast("Save file failed")
        }
    }

    companion object {
        fun newInstance(chatId: Int, imageUrl: String?): SlidingImageDialogFragment {
            val fragment = SlidingImageDialogFragment()
            fragment.arguments = Bundle().apply {
                putInt("chatId", chatId)
                putString("imageUrl", imageUrl)
            }
            return fragment
        }
    }
}
