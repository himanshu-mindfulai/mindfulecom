package com.mindfulai.Utils;


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mindfulai.Models.Notification
import com.mindfulai.dao.AppDatabase
import com.mindfulai.ministore.R
import java.util.*


class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            Log.i("FcmNotification", message?.notification.toString())
            Log.i("FcmData", message?.data.toString())
            createNotificationChannel(message)
            var builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getBuilder(message)
            } else {
                null
            }
            with(NotificationManagerCompat.from(this)) {
                builder?.build()?.let { notify(2, it) }
                Log.i("fcmNotification", "sent")
            }
            saveNotification(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun saveNotification(message: RemoteMessage) {
        var notification = Notification(
                Math.random().toLong(),
                message.data["title"].toString(),
                message.data["body"].toString(),
                message.data["image"] ?: "",
                Date().toString())
        AppDatabase.getDatabase(applicationContext)?.notificationDao()?.saveNotification(notification)
        Log.i("notifications##", AppDatabase.getDatabase(applicationContext)?.notificationDao()?.getAllNotifications().toString() + "####")
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBuilder(message: RemoteMessage): NotificationCompat.Builder? {
        var builder: NotificationCompat.Builder? = null
        builder = message?.data?.get("channel_id")?.let {
            NotificationCompat.Builder(this, it)
                    .setSmallIcon(R.drawable.granny_logo)
                    .setContentTitle(message?.data?.get("title"))
                    .setContentText(message?.data?.get("body"))
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(message?.data?.get("body")))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLargeIcon(Glide.with(this)
                            .asBitmap()
                            .load(R.drawable.ic_notification)
                            .submit()
                            .get())
        }
        if (message.data?.get("image") != null) {
            builder?.setLargeIcon(Glide.with(this)
                    .asBitmap()
                    .load(GlobalEnum.AMAZON_URL+message?.data?.get("image"))
                    .submit()
                    .get())
                    ?.setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(Glide.with(this)
                                    .asBitmap()
                                    .load(GlobalEnum.AMAZON_URL+message?.data?.get("image"))
                                    .submit()
                                    .get())
                            .bigLargeIcon(null))
        }
        return builder
    }

    private fun createNotificationChannel(message: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                    message?.data?.get("channel_id"),
                    CommonUtils.capitalizeWord(message?.data?.get("channel_id")),
                    importance).apply {
                description = "Push notifications"
            }
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("TAG", token)
    }
}
