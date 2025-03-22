package com.example.echochat.util

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.echochat.R

object BindingUtils {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun ImageView.setImageUrl(imageUrl: String?) {
        val absoluteImageUrl = BASE_URL_GET_IMAGE + imageUrl
        Glide.with(this.context)
            .load(absoluteImageUrl.takeIf { it.isNotEmpty() } ?: R.drawable.ic_person)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .into(this)
    }

}

@BindingAdapter("setRefreshing")
fun SwipeRefreshLayout.setRefreshing(uiState: UiState<Nothing>) {
    this.apply {
        isRefreshing = when (uiState) {
            UiState.Loading -> true
            else -> false
        }
    }
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.onRefresh(func: () -> Unit?) {
    this.apply {
        setOnRefreshListener {
            func()
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

@BindingAdapter("showOnNoData")
fun View.showOnNoData(uiState: UiState<Nothing>) {
    this.apply {
        isVisible = when (uiState) {
            UiState.NoData -> true
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














