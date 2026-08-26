package com.example.chatease.data.repository

import com.example.chatease.domain.repository.FcmTokenProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FcmTokenProviderImpl @Inject constructor() : FcmTokenProvider {

    override suspend fun deleteToken() {
        FirebaseMessaging.getInstance().deleteToken().await()
    }

    override suspend fun getToken(): String {
        return FirebaseMessaging.getInstance().token.await()
    }
}