package com.example.echochat.ui.friends

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.databinding.ItemHeaderBinding
import com.example.echochat.databinding.ItemUserBinding
import com.example.echochat.model.FriendRequest
import com.example.echochat.model.dto.FriendRequestDTO
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.VIEW_TYPE_HEADER
import com.example.echochat.util.VIEW_TYPE_ITEM
import com.example.echochat.util.getFriend

class FriendRequestListAdapter(
    private val viewModel: FriendsViewModel? = null,
) : ListAdapter<Any, RecyclerView.ViewHolder>(FriendRequestDiffCallback()) {

    private var groupedFriends: Map<Char, List<FriendRequestDTO>> = emptyMap()
    private var displayList: List<Any> = emptyList()

    fun submitGroupedList(friends: List<FriendRequestDTO>) {
        groupedFriends = friends.groupBy { it.getFriend().name.first().uppercaseChar() }.toSortedMap()
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
            val otherUser = friendRequest.getFriend()
            bind.tvUserName.text = otherUser.name
            bind.imageProfile.setImageUrl(otherUser.profileImageUrl)
            bind.tvUserStatus.text = when(friendRequest.requestStatus){
                FriendRequest.RequestStatus.ACCEPTED -> "Hãy bắt đầu trò chuyện"
                FriendRequest.RequestStatus.REJECTED -> "Gửi lời mời để kết bạn"
                else -> if (friendRequest.sender.id == MY_USER_ID) "Đã gửi lời mời kết bạn" else "Nhận lời mời kết bạn"
            }

            bind.btnAction1.setOnClickListener {
                val friendRequestDTOUpdate = friendRequest.copy().apply {
                    requestStatus = FriendRequest.RequestStatus.REJECTED
                    if (sender.id != MY_USER_ID) {
                        sender = receiver.also { receiver = sender }
                    }
                }
                viewModel?.updateFriendRequest(friendRequestDTOUpdate)
            }

            bind.imageProfile.setOnClickListener {
                viewModel?.openFriendProfile(friendRequest)
                viewModel?.getChatId(MY_USER_ID, friendRequest.getFriend().id!!)
            }

            bind.btnAction2.setOnClickListener {
                val friendRequestDTOUpdate = friendRequest.copy().apply {
                    requestStatus = when (friendRequest.requestStatus) {
                        FriendRequest.RequestStatus.PENDING -> if (sender.id == MY_USER_ID) FriendRequest.RequestStatus.REJECTED else FriendRequest.RequestStatus.ACCEPTED
                        FriendRequest.RequestStatus.REJECTED -> FriendRequest.RequestStatus.PENDING
                        else -> requestStatus
                    }
                    if (sender.id != MY_USER_ID && requestStatus == FriendRequest.RequestStatus.PENDING) {
                        sender = receiver.also { receiver = sender }
                    }
                    if (sender.id != MY_USER_ID && requestStatus == FriendRequest.RequestStatus.ACCEPTED) {
                        sender = receiver.also { receiver = sender }
                    }
                }
                viewModel?.updateFriendRequest(friendRequestDTOUpdate)
                if(friendRequestDTOUpdate.requestStatus == FriendRequest.RequestStatus.PENDING){
                    viewModel?.sendNotification(friendRequestDTOUpdate)
                }
            }

            when(friendRequest.requestStatus){
                FriendRequest.RequestStatus.ACCEPTED -> {
                    bind.btnAction1.visibility = View.GONE
                    bind.btnAction2.visibility = View.GONE
                }

                FriendRequest.RequestStatus.REJECTED -> {
                    bind.apply {
                        btnAction1.visibility = View.GONE
                        btnAction2.apply {
                            visibility = View.VISIBLE
                            setIconResource(R.drawable.ic_add_friend)
                            iconTint = root.resources.getColorStateList(R.color.colorWhite, null)
                            backgroundTintList = root.resources.getColorStateList(R.color.colorPrimary, null)
                        }
                    }
                }

                else -> {
                    bind.apply {
                        if(friendRequest.sender.id == MY_USER_ID){
                            btnAction1.visibility = View.GONE
                            btnAction2.apply {
                                visibility = View.VISIBLE
                                setIconResource(R.drawable.ic_remove)
                                iconTint = root.resources.getColorStateList(R.color.colorPrimary, null)
                                backgroundTintList = root.resources.getColorStateList(R.color.colorWhite, null)
                            }
                        } else {
                            btnAction1.apply {
                                visibility = View.VISIBLE
                            }
                            btnAction2.apply {
                                visibility = View.VISIBLE
                                setIconResource(R.drawable.ic_accepted)
                                iconTint = root.resources.getColorStateList(R.color.colorWhite, null)
                                backgroundTintList = root.resources.getColorStateList(R.color.colorPrimary, null)
                            }
                        }
                    }
                }
            }
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
