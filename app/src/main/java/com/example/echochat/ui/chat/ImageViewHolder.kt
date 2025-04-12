package com.example.echochat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.echochat.databinding.ItemImageBinding
import com.example.echochat.util.BindingUtils.setImageUrl

class ImageViewHolder private constructor(
    private val binding: ItemImageBinding
) : ViewHolder(binding.root) {

    fun bind(item: String) {
        binding.apply {
            imageView.setImageUrl(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup): ImageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemImageBinding.inflate(inflater, parent, false)
            return ImageViewHolder(binding)
        }
    }
}