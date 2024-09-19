package com.example.android_live_notification

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.FlutterEngine


class MainActivity: FlutterActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    private val flutterChannel = "androidInteractiveNotifications"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, flutterChannel).setMethodCallHandler {
                call, result ->
            if (call.method == "startNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val args = call.arguments<Map<String, Any>>()
                    val progress = args?.get("progress") as? Int
                    val minutes = args?.get("minutesToDelivery") as? Int

                    if( progress != null && minutes != null){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LiveNotificationManager(context).showNotification(progress,minutes)
                        }
                    }
                }
                result.success("Notification displayed")
            }
            else if (call.method == "updateNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val args = call.arguments<Map<String, Any>>()
                    val progress = args?.get("progress") as? Int
                    val minutes = args?.get("minutesToDelivery") as? Int
                    if(progress != null && minutes != null){
                        LiveNotificationManager(context)
                            .updateNotification(currentProgress =  progress, minutesToDelivery = minutes)
                    }
                }
            }
            else if (call.method == "finishDeliveryNotification") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LiveNotificationManager(context)
                        .finishDeliveryNotification()
                }
                result.success("Notification delivered")
            }
            else if (call.method == "endNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LiveNotificationManager(context)
                        .endNotification()
                }
                result.success("Notification cancelled")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, permissions, 200)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LiveNotificationManager(context).endNotification()
        }
    }
}

