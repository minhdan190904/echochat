package com.example.echochat.ui.friends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.databinding.ItemHeaderBinding
import com.example.echochat.databinding.ItemUserBinding
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.getDisplayName

const val VIEW_TYPE_HEADER = 0
const val VIEW_TYPE_ITEM = 1

class FriendRequestListAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(FriendRequestDiffCallback()) {

    private var groupedFriends: Map<Char, List<FriendRequestDTO>> = emptyMap()
    private var displayList: List<Any> = emptyList()

    fun submitGroupedList(friends: List<FriendRequestDTO>) {
        groupedFriends = friends.groupBy { it.getDisplayName().first().uppercaseChar() }.toSortedMap()
        displayList = groupedFriends.flatMap { (key, value) -> listOf(key) + value }
        submitList(displayList)
    }

    override fun getItemViewType(position: Int): Int {
        return if (displayList[position] is Char) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(
                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            FriendRequestViewHolder(
                ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(displayList[position] as Char)
        } else if (holder is FriendRequestViewHolder) {
            holder.bind(displayList[position] as FriendRequestDTO)
        }
    }

    class HeaderViewHolder(private val bind: ItemHeaderBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(letter: Char) {
            bind.headerTitle.text = letter.toString()
        }
    }

    inner class FriendRequestViewHolder(private val bind: ItemUserBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(friendRequest: FriendRequestDTO) {
            val otherUser =
                if (MY_USER_ID == friendRequest.sender.id) friendRequest.receiver else friendRequest.sender
            bind.tvUserName.text = otherUser.name
            bind.imageProfile.setImageUrl(otherUser.profileImageUrl)
        }
    }

    class FriendRequestDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }
}
