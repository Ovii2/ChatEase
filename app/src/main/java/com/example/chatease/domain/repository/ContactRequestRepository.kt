package com.example.chatease.domain.repository

import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.ContactRequestCooldown
import kotlinx.coroutines.flow.Flow

interface ContactRequestRepository {

    suspend fun sendContactRequest(senderUserId: String, receiverUserId: String)

    suspend fun getPendingRequests(currentUserId: String): List<ContactRequest>

    fun observePendingRequests(currentUserId: String): Flow<List<ContactRequest>>

    fun observeSentRequests(currentUserId: String): Flow<List<ContactRequest>>

    suspend fun getSentRequests(currentUserId: String): List<ContactRequest>

    suspend fun withdrawContactRequest(
        requestId: String,
        senderUserId: String,
        receiverUserId: String
    )

    suspend fun createCooldown(senderUserId: String, receiverUserId: String)

    suspend fun isCooldownActive(
        senderUserId: String,
        receiverUserId: String
    ): Boolean

    suspend fun getCooldown(
        senderUserId: String,
        receiverUserId: String
    ): ContactRequestCooldown?

    suspend fun acceptContactRequest(requestId: String)

    suspend fun declineContactRequest(requestId: String)

}
