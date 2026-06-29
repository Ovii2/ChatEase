package com.example.chatease.data.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.chatease.domain.repository.ContactRequestRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactRequestActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var contactRequestRepository: ContactRequestRepository

    companion object {
        const val EXTRA_REQUEST_ID = "requestId"
        const val EXTRA_NOTIFICATION_ID = "notificationId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val requestId = intent.getStringExtra(EXTRA_REQUEST_ID) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)

        CoroutineScope(Dispatchers.IO).launch {
            contactRequestRepository.declineContactRequest(requestId)
        }

        if (notificationId != -1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(notificationId)
        }
    }
}