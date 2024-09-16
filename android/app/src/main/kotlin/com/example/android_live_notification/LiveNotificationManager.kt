package com.example.android_live_notification


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

class LiveNotificationManager(private val context: Context)  {
    private val remoteViews = RemoteViews("com.example.android_live_notification", R.layout.live_notification)
    private val  channelId = "notificationChannel"
    private val  notificationId = 100
    private val pendingIntent = PendingIntent.getActivity(
        context,
        200,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() : NotificationManager{
        val channel =
            NotificationChannel(channelId, "App Delivery Notification", NotificationManager.IMPORTANCE_DEFAULT).apply {
                setSound(null, null)
                vibrationPattern = longArrayOf(0L)
            }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }

    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(currentProgress: Int, minutesToDelivery: Int)  : Notification{

        val notificationManager =  createNotificationChannel()

        val notification = Notification.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentTitle("Live Notification")
            .setContentIntent(pendingIntent)
            .setContentText("Your delivery comes in $minutesToDelivery minutes")
            .setCustomBigContentView(remoteViews)
            .build()
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery minutes")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
        return notification
    }

    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotification(currentProgress: Int,minutesToDelivery: Int) {
        val notificationManager =  createNotificationChannel()

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Notification.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle("Live Notification")
                .setContentText("Your delivery comes in $minutesToDelivery minutes")
                .setCustomBigContentView(remoteViews)
                .build()
        } else {
            return
        }

        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery minutes")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
    }


    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun finishDeliveryNotification() {
        val notificationManager =  createNotificationChannel()
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Notification.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("Live Notification")
                .setContentText("Your delivery arrive")
                .setCustomBigContentView(remoteViews)
                .build()
        } else {
            return
        }
        remoteViews.setTextViewText(R.id.delivery_message, "Your delivery Arrive")
        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery_arrive)
        remoteViews.setViewVisibility(R.id.progress, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_text, View.GONE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.GONE)
        remoteViews.setTextViewText(R.id.delivery_subtitle,"Enjoy your delivery :)")
        notificationManager.notify(notificationId, notification)
    }

    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun endNotification() {
        val notificationManager = createNotificationChannel()
        notificationManager.cancel(notificationId)
        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
        remoteViews.setTextViewText(R.id.delivery_subtitle,"Your delivery is coming")
        remoteViews.setViewVisibility(R.id.progress, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.progress_text, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.VISIBLE)
        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery1)
    }
}