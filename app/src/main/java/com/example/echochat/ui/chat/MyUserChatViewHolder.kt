package com.example.echochat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.databinding.ItemChatMeBinding
import com.example.echochat.model.Message


class MyUserChatViewHolder private constructor(private val binding: ItemChatMeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Message) {
        binding.apply {
            listItem = item
            imageMessage.isVisible = when (item.messageType) {
                Message.MessageType.IMAGE -> true
                Message.MessageType.VIDEO -> true
                else -> false
            }
            tvMessage.isVisible = item.messageType == Message.MessageType.TEXT
            binding.tvMessage.setOnClickListener {
                binding.tvTimeSent.isVisible = !binding.tvTimeSent.isVisible
            }

            binding.imageMessage.setOnClickListener {
                binding.tvTimeSent.isVisible = !binding.tvTimeSent.isVisible
            }
            executePendingBindings()
        }

    }

    companion object {
        fun from(parent: ViewGroup): MyUserChatViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemChatMeBinding.inflate(inflater, parent, false)
            return MyUserChatViewHolder(binding)
        }
    }
}