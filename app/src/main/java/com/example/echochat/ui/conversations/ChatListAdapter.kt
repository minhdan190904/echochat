import android.annotation.SuppressLint
import android.util.Log
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
import com.example.echochat.util.MY_USER_ID

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
        fun bind(chat: Chat) {
            val otherUser = if (chat.user1.id == MY_USER_ID) chat.user2 else chat.user1

            chat.getLastMessage()?.let { msg ->
                bind.tvUserLastMessage.text = if(msg.messageType == Message.MessageType.IMAGE) {
                    if(msg.sender?.id == MY_USER_ID) {
                        bind.root.context.getString(R.string.last_message_me_image)
                    } else {
                        bind.root.context.getString(R.string.last_message_other_image, msg.sender?.name)
                    }
                } else {
                    if (msg.sender?.id == MY_USER_ID) {
                        bind.root.context.getString(R.string.last_message_me, msg.message)
                    } else {
                        bind.root.context.getString(R.string.last_message_other, msg.sender?.name, msg.message)
                    }
                }
            }

            bind.imageProfile.setImageUrl(otherUser.profileImageUrl)
            bind.tvUserLastMessageDate.text = chat.getLastMessage()?.sendingTime.toString()
            bind.tvUserName.text = otherUser.name

            if(chat.getLastMessage() == null) {
                bind.tvUserLastMessage.text = "Let start a chat"
                bind.tvUserLastMessageDate.text = ""
                bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
            }

            chat.getLastMessage()?.let { msg ->
                if(!msg.isSeen && msg.sender?.id != MY_USER_ID) {
                    bind.tvUserLastMessageDate.setTypeface(null, android.graphics.Typeface.BOLD)
                    bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    bind.tvUserLastMessageDate.setTypeface(null, android.graphics.Typeface.NORMAL)
                    bind.tvUserLastMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }

            val userOnlineIconColor = if (otherUser.isOnline) {
                bind.root.resources.getColor(android.R.color.holo_green_light, null)
            } else {
                bind.root.resources.getColor(android.R.color.darker_gray, null)
            }
            bind.iconOnline.setBackgroundColor(userOnlineIconColor)

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
