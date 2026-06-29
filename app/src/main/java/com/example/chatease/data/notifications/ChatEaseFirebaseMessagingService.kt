package com.example.chatease.data.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.app.NotificationCompat
import androidx.core.graphics.createBitmap
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
import kotlin.random.Random

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
        val fallbackAvatar = createInitialBitmap(senderName.firstOrNull()?.uppercase() ?: "?")
        val requestId = remoteMessage.data["requestId"] ?: return
        val notificationId = requestId.hashCode()

        val bitmap = senderAvatar?.takeIf { it.isNotBlank() }?.let { url ->
            imageLoader.execute(
                ImageRequest.Builder(this)
                    .data(url)
                    .allowHardware(false)
                    .build()
            ).image?.toBitmap()
        } ?: fallbackAvatar

        val acceptPendingIntent = createActionPendingIntent(
            requestId = requestId,
            notificationId = notificationId,
            action = ContactRequestActionReceiver.ACTION_ACCEPT
        )

        val declinePendingIntent = createActionPendingIntent(
            requestId = requestId,
            notificationId = notificationId,
            action = ContactRequestActionReceiver.ACTION_DECLINE
        )

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
            .addAction(0, getString(R.string.accept), acceptPendingIntent)
            .addAction(0, getString(R.string.decline), declinePendingIntent)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notification)
    }

    private fun createInitialBitmap(
        letter: String
    ): Bitmap {
        val size = 128

        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        val avatarColor = Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = avatarColor
        }

        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size / 2f,
            circlePaint
        )

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 56f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val y = size / 2f - (textPaint.descent() + textPaint.ascent()) / 2

        canvas.drawText(
            letter,
            size / 2f,
            y,
            textPaint
        )

        return bitmap
    }

    private fun createActionPendingIntent(
        requestId: String,
        notificationId: Int,
        action: String
    ): PendingIntent {
        val intent = Intent(this, ContactRequestActionReceiver::class.java).apply {
            putExtra(ContactRequestActionReceiver.EXTRA_REQUEST_ID, requestId)
            putExtra(ContactRequestActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(ContactRequestActionReceiver.EXTRA_ACTION, action)
        }

        return PendingIntent.getBroadcast(
            this,
            "${requestId}_$action".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}
