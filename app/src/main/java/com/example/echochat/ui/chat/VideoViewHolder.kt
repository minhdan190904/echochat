package com.example.echochat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.echochat.databinding.ItemVideoBinding
import com.example.echochat.util.BASE_URL_GET_IMAGE

class VideoViewHolder private constructor(
    private val binding: ItemVideoBinding
) : ViewHolder(binding.root) {

    fun bind(item: String, context: Context) {
        //set up exoplayer
        val player = ExoPlayer.Builder(context).build()
        binding.videoView.player = player
        val mediaItem = MediaItem.fromUri(BASE_URL_GET_IMAGE + item)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    companion object {
        fun from(parent: ViewGroup): VideoViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemVideoBinding.inflate(inflater, parent, false)
            return VideoViewHolder(binding)
        }
    }
}