package com.example.echochat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.databinding.ItemChatMeBinding
import com.example.echochat.model.Message
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.hide
import com.example.echochat.util.show

class MyUserChatViewHolder private constructor(private val binding: ItemChatMeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Message, viewModel: ChatViewModel?) {
        binding.apply {
            listItem = item
            imageMessage.isVisible = when (item.messageType) {
                Message.MessageType.IMAGE -> true
                Message.MessageType.VIDEO -> true
                else -> false
            }

            if(item.messageType == Message.MessageType.IMAGE) {
                imageMessage.setImageUrl(item.message)
            } else if (item.messageType == Message.MessageType.VIDEO) {
                imageMessage.setImageUrl(item.message)
            }

            tvMessage.isVisible = item.messageType == Message.MessageType.TEXT
            binding.tvMessage.setOnClickListener {
                binding.tvTimeSent.isVisible = !binding.tvTimeSent.isVisible
            }

            binding.imageMessage.setOnClickListener {
                binding.tvTimeSent.isVisible = !binding.tvTimeSent.isVisible
                viewModel?.openSlingImage(item.message)
            }

            if(item.isUploading){
                showLoadingState()
            } else {
                hideLoadingState()
            }
            executePendingBindings()
        }

    }

    private fun showLoadingState() {
        binding.loadingChip.show()
        binding.imageMessage.hide()
        binding.tvMessage.hide()
    }

    private fun hideLoadingState() {
        binding.loadingChip.hide()
    }

    companion object {
        fun from(parent: ViewGroup): MyUserChatViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemChatMeBinding.inflate(inflater, parent, false)
            return MyUserChatViewHolder(binding)
        }
    }
}