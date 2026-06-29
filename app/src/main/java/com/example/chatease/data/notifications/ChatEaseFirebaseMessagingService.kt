package com.example.chatease.data.notifications

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.example.chatease.R
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatEaseFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val CONNECTIONS_CHANNEL_ID = "connections_notifications"
        const val CONNECTIONS_CHANNEL_NAME = "Connections Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        serviceScope.launch {
            showConnectionNotification(remoteMessage)
        }
    }

    private suspend fun showConnectionNotification(remoteMessage: RemoteMessage) {
        val title = getString(R.string.new_connection_request)

        val senderName = remoteMessage.data["senderName"] ?: getString(R.string.app_name)

        val body = getString(
            R.string.connection_request,
            senderName
        )

        val senderAvatar = remoteMessage.data["senderAvatar"]
        Log.d("FCM", "Sender avatar url: $senderAvatar")
        val bitmap = senderAvatar?.takeIf { it.isNotBlank() }?.let { url ->
            imageLoader.execute(
                ImageRequest.Builder(this)
                    .data(url)
                    .allowHardware(false)
                    .build()
            ).image?.toBitmap()
        }

        val notification = NotificationCompat.Builder(this, CONNECTIONS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setBigContentTitle(title)
            )
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}