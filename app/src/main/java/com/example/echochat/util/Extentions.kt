package com.example.echochat.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.echochat.model.User
import com.example.echochat.model.dto.FriendRequestDTO
import com.example.echochat.network.NetworkResource
import retrofit2.HttpException
import java.io.IOException
import java.text.DateFormatSymbols
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

fun Activity.toast(content: String){
    Toast.makeText(this, content, Toast.LENGTH_LONG).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun generateTimeNow(): String {
    val symbols = DateFormatSymbols(Locale.ENGLISH)
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.dateFormatSymbols = symbols
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

suspend fun <T> handleNetworkCall(
    call: suspend () -> T,
    customErrorMessages: Map<Int, String> = emptyMap()
): NetworkResource<T> {
    return try {
        val response = call()
        NetworkResource.Success(response)
    } catch (ex: HttpException) {
        val defaultMessages = mapOf(
            404 to "Not found",
            500 to "Internal Server Error. Please try again later."
        )
        val errorMessage = customErrorMessages[ex.code()] ?: defaultMessages[ex.code()] ?: "Server error: ${ex.message()}"
        NetworkResource.Error(message = errorMessage, responseCode = ex.code())
    } catch (ex: IOException) {
        NetworkResource.NetworkException("Network error. Please check your connection.")
    } catch (ex: Exception) {
        NetworkResource.Error(ex.message ?: "Unexpected error")
    }
}

var myUser: User?
    get() = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(USER_SESSION, value)
    }

var myFriend: User?
    get() = SharedPreferencesReManager.getData(FRIEND_SESSION, User::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(FRIEND_SESSION, value)
    }

var tokenApi: String?
    get() = SharedPreferencesReManager.getData(TOKEN_KEY, String::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(TOKEN_KEY, value)
    }

var tokenUserDevice: String?
    get() = SharedPreferencesReManager.getData(TOKEN_USER_DEVICE, String::class.java)
    set(value){
        SharedPreferencesReManager.saveData(TOKEN_USER_DEVICE, value)
    }

