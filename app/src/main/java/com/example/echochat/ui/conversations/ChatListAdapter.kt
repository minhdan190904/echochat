import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.databinding.ItemConversationBinding
import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.formatMessageDate
import com.example.echochat.util.hide
import com.example.echochat.util.myUser
import com.example.echochat.util.show

class ChatListAdapter : ListAdapter<Chat, ChatListAdapter.ChatListViewHolder>(ChatDiffCallback()) {

    var setOnClick: ((Chat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val bind = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatListViewHolder(private val bind: ItemConversationBinding) :
        RecyclerView.ViewHolder(bind.root) {
        @SuppressLint("SetTextI18n")
        fun bind(chat: Chat) {
            val otherUser = if (chat.user1.id == myUser?.id) chat.user2 else chat.user1
            chat.messageList.sortBy { it.sendingTime }
            chat.getLastMessage()?.let { msg ->
                bind.tvUserLastMessage.text = if(msg.messageType == Message.MessageType.IMAGE) {
                    if(msg.sender?.id == myUser?.id) {
                        bind.root.context.getString(R.string.last_message_me_image)
                    } else {
                        bind.root.context.getString(R.string.last_message_other_image, msg.sender?.name)
                    }
                } else if(msg.messageType == Message.MessageType.TEXT) {
                    if (msg.sender?.id == myUser?.id) {
                        bind.root.context.getString(R.string.last_message_me, msg.message)
                    } else {
                        bind.root.context.getString(R.string.last_message_other, msg.sender?.name, msg.message)
                    }
                } else {
                    if(msg.sender?.id == myUser?.id) {
                        bind.root.context.getString(R.string.last_message_me_video)
                    } else {
                        bind.root.context.getString(R.string.last_message_other_video, msg.sender?.name)
                    }
                }
            }

            bind.imageProfile.setImageUrl(otherUser.profileImageUrl)
            bind.tvUserLastMessageDate.text = "• " + formatMessageDate(chat.getLastMessage()?.sendingTime)
            bind.tvUserName.text = otherUser.name

            if(chat.getLastMessage() == null) {
                bind.tvUserLastMessage.text = bind.root.context.getString(R.string.let_start_a_chat)
                bind.tvUserLastMessageDate.text = ""
                bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
            }

            chat.getLastMessage()?.let { msg ->
                if(!msg.isSeen && msg.sender?.id != myUser?.id) {
                    bind.tvUserLastMessageDate.setTypeface(null, android.graphics.Typeface.BOLD)
                    bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    bind.tvUserLastMessageDate.setTypeface(null, android.graphics.Typeface.NORMAL)
                    bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }

            bind.iconOnline.apply {
                if (otherUser.isOnline) {
                    show()
                    setBackgroundColor(bind.root.resources.getColor(android.R.color.holo_green_light, null))
                } else {
                    hide()
                }
            }

            bind.root.setOnClickListener {
                setOnClick?.invoke(chat)
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem == newItem
    }
}
