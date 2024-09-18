package com.example.android_live_notification


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

class LiveNotificationManager(private val context: Context)  {
    private val remoteViews = RemoteViews("com.example.android_live_notification", R.layout.live_notification)
    private val  channelWithHighPriority = "channelWithHighPriority"
    private val  channelWithDefaultPriority = "channelWithDefaultPriority"
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
    fun createNotificationChannel(channelName : String, importanceHigh : Boolean = false) : NotificationManager{
        val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
        val existingChannel =  (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getNotificationChannel(channelName)
        return if(existingChannel == null){
            val channel =
                NotificationChannel(channelName, "App Delivery Notification", importance).apply {
                    setSound(null, null)
                    vibrationPattern = longArrayOf(0L)
                }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager
        }else{
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onFirstNotification( minutesToDelivery: Int ) : Notification{
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        return  Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Live Notification - Delivery out for shipment")
            .setContentIntent(pendingIntent)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onGoingNotification(minutesToDelivery: Int ) : Notification{
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        return  Notification.Builder(context, channelWithDefaultPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setOngoing(true)
            .setContentTitle("Live Notification - Delivery on the way")
            .setContentIntent(pendingIntent)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onFinishNotification() : Notification{
        return Notification.Builder(context,channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentTitle("Live Notification - Arrives")
            .setContentText("Your delivery arrive")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(currentProgress: Int, minutesToDelivery: Int)  : Notification{

        val notificationManager =  createNotificationChannel(channelWithHighPriority,importanceHigh = true)

        val notification = onFirstNotification(minutesToDelivery)
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery minutes")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
        return notification
    }

    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotification(currentProgress: Int,minutesToDelivery: Int) {
        val notificationManager =  createNotificationChannel(channelWithDefaultPriority)
        val notification =  onGoingNotification(minutesToDelivery)
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
    }


    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun finishDeliveryNotification() {

        val notificationManager =  createNotificationChannel(channelWithHighPriority,importanceHigh = true)
        val notification = onFinishNotification()
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
      val notificationManager =   context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelWithHighPriority)
        notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
        remoteViews.setTextViewText(R.id.delivery_subtitle,"Your delivery is coming")
        remoteViews.setViewVisibility(R.id.progress, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.progress_text, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.VISIBLE)
        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery1)
    }
}