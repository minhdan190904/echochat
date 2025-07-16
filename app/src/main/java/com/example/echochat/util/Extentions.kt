package com.example.echochat.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun View.hide(){
    visibility = View.INVISIBLE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun Date.equalsTime(other: Date): Boolean {
    return this.toString() == other.toString()
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

fun Fragment.intentUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
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

fun View.setAnimationRotate(time: Long, rotateValue: Float){
    disable()
    animate().rotationBy(rotateValue).setDuration(time).start()
    Handler(Looper.getMainLooper()).postDelayed({
        enabled()
    }, time)
}

fun View.disable(){
    isEnabled = false
}

fun View.enabled(){
    isEnabled = true
}

fun formatMessageDate(sendingTime: Date?): String {
    if (sendingTime == null) return ""
    val sendingDateTime = LocalDateTime.ofInstant(sendingTime.toInstant(), java.time.ZoneId.systemDefault())
    val sendingDate = sendingDateTime.toLocalDate()
    val now = LocalDateTime.now()
    val currentDate = now.toLocalDate()

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    val fullDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return when {
        currentDate.year > sendingDate.year -> sendingDateTime.format(fullDateFormatter)
        currentDate.dayOfYear - sendingDate.dayOfYear > 7 || currentDate.dayOfYear < sendingDate.dayOfYear -> sendingDateTime.format(dateFormatter)
        currentDate.dayOfYear != sendingDate.dayOfYear -> sendingDateTime.format(dayOfWeekFormatter)
        else -> sendingDateTime.format(timeFormatter)
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

