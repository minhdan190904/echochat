package com.example.echochat.ui.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.model.Message
import com.example.echochat.util.myUser

class ChatAdapter(
    private val viewModel: ChatViewModel? = null,
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_chat_me -> MyUserChatViewHolder.from(parent)
            R.layout.item_chat_other_person -> OtherUserChatViewHolder.from(parent)
            else -> OtherUserChatViewHolder.from(parent)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        val item = getItem(position)

        item?.let {
            when (itemViewType) {
                R.layout.item_chat_me -> (holder as MyUserChatViewHolder).bind(it, viewModel, itemCount, position)
                R.layout.item_chat_other_person -> (holder as OtherUserChatViewHolder).bind(it, viewModel)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {

        return when (getItem(position).sender?.id) {
            myUser?.id -> R.layout.item_chat_me
            else -> R.layout.item_chat_other_person

        }
    }

    internal class MessageItemCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

}
