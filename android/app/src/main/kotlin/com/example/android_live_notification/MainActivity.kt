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
    private val liveActivityManager = LiveNotificationManager(context)
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
                        liveActivityManager.showNotification(currentProgress =  progress, minutesToDelivery = minutes)
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
                        liveActivityManager.updateNotification(currentProgress =  progress, minutesToDelivery = minutes)
                    }
                }
            }
            else if (call.method == "endNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    liveActivityManager.endNotification()
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

}
