package com.example.chatease.di

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ChatEase : Application(), DefaultLifecycleObserver {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firebase: FirebaseFirestore

    @Inject
    lateinit var userRepository: UserRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun onCreate() {
        super<Application>.onCreate()

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        val currentUserId = auth.currentUser?.uid ?: return

        applicationScope.launch {
            userRepository.updateUserStatus(
                userId = currentUserId,
                status = UserPresenceStatus.ONLINE
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        val currentUserId = auth.currentUser?.uid ?: return

        applicationScope.launch {
            userRepository.updateUserStatus(
                userId = currentUserId,
                status = UserPresenceStatus.AWAY
            )
        }
    }
}
