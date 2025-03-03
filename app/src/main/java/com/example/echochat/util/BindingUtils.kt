package com.example.echochat.util

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.echochat.R

object BindingUtils {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun ImageView.setImageUrl(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty().not()) {
            Glide.with(this.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_person)
                .into(this)

        }
    }
}


@BindingAdapter("showOnLoading")
fun View.showOnLoading(uiState: UiState<Nothing>) {
    this.apply {
        isVisible = when (uiState) {
            UiState.Loading -> true
            else -> false

        }
    }
}

@BindingAdapter("showOnHasData")
fun View.showOnHasData(uiState: UiState<Nothing>) {
    this.apply {
        isVisible = when (uiState) {
            UiState.HasData -> true
            else -> false

        }
    }
}














