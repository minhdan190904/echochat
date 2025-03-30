package com.example.echochat.util

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.echochat.model.User
import com.example.echochat.model.dto.FriendRequestDTO
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

fun Date.formatOnlyDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun String.toDate(): Date? {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.parse(this)
}

fun FriendRequestDTO.getFriend(): User {
    return if (myUser?.id == this.sender.id) this.receiver else this.sender
}

fun Date.customLastSeenChat(): String {
    return when(val duration = Date().time - this.time - 3600*1000*7){
        in 0..60000 - 1 -> "Vừa xong"
        in 60000..3600000 - 1 -> "${duration/60000} phút trước"
        in 3600000..86400000 -1 -> "${duration/3600000} giờ trước"
        else -> this.formatOnlyDate()
    }
}

var myUser: User?
    get() = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(USER_SESSION, value)
    }

