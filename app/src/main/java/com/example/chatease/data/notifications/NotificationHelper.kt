package com.example.chatease.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationHelper(
    private val context: Context
) {
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ChatEaseFirebaseMessagingService.CONNECTIONS_CHANNEL_ID,
                ChatEaseFirebaseMessagingService.CONNECTIONS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
