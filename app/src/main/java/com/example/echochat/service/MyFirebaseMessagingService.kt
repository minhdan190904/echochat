package com.example.echochat

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.echochat.util.BASE_URL_GET_IMAGE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.ExecutionException
import android.graphics.Bitmap as Bitmap1

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "New Message"
        val message = remoteMessage.notification?.body ?: "You have a new notification"
        val imageUrl = remoteMessage.notification?.imageUrl?.toString()

        sendNotification(title, message, imageUrl)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(title: String, message: String, imageUrl: String?) {
        val channelId = "notification_channel"
        val notificationId = 101

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.chat)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (!imageUrl.isNullOrEmpty()) {
            val bitmap = getBitmapFromUrl(BASE_URL_GET_IMAGE + imageUrl)
            if (bitmap != null) {
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Bitmap1?)
                )
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FCM Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "This channel is used for FCM notifications"
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap1? {
        return try {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}
