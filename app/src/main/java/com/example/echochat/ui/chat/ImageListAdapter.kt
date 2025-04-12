package com.example.echochat.ui.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.util.myUser

class ImageListAdapter : ListAdapter<String, RecyclerView.ViewHolder>(ImageItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_image -> ImageViewHolder.from(parent)
            else -> VideoViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        val item = getItem(position)

        item?.let {
            when (itemViewType) {
                R.layout.item_image -> (holder as ImageViewHolder).bind(it)
                R.layout.item_video -> (holder as VideoViewHolder).bind(it, holder.itemView.context)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.endsWith(".mp4", ignoreCase = true)) {
            R.layout.item_video
        } else {
            R.layout.item_image
        }
    }


    internal class ImageItemCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}