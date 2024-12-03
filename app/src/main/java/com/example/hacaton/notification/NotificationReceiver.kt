package com.example.hacaton.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.hacaton.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val subject = intent.getStringExtra("subject") ?: return
        val room = intent.getStringExtra("room") ?: return
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

        if(notificationsEnabled) {
            val notification = NotificationCompat.Builder(context, "schedule_channel")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Скоро начало пары")
                .setContentText("$subject через 5 минут в аудитории $room")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(subject.hashCode(), notification)
        }

    }
}