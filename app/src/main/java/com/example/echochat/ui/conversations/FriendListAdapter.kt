import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.databinding.ItemUserStoryBinding
import com.example.echochat.model.User
import com.example.echochat.util.BindingUtils.setImageUrl

class FriendListAdapter : ListAdapter<User, FriendListAdapter.FriendListViewHolder>(UserDiffCallback()) {

    var setOnClick: ((User) -> Unit)? = null

    inner class FriendListViewHolder(private val bind: ItemUserStoryBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(friend: User) {
            bind.imageViewProfile.setImageUrl(friend.profileImageUrl)
            bind.tvUserName.text = friend.name
            bind.root.setOnClickListener {
                setOnClick?.invoke(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val bind = ItemUserStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendListViewHolder(bind)
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}
