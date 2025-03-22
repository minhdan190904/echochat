package com.example.echochat.util

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.echochat.model.FriendRequestDTO
import com.example.echochat.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun View.hide(){
    visibility = View.INVISIBLE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun Fragment.toast(content: String){
    Toast.makeText(context, content, Toast.LENGTH_LONG).show()
}


fun generateTime(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}

fun Fragment.intentActivity(destination: Class<out Activity>) {
    val intent = Intent(requireContext(), destination)
    startActivity(intent)
}


fun FriendRequestDTO.getFriend(): User {
    return if (MY_USER_ID == this.sender.id) this.receiver else this.sender
}

