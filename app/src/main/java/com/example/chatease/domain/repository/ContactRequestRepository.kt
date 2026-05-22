package com.example.chatease.domain.repository

import com.example.chatease.domain.model.ContactRequest

interface ContactRequestRepository {

    suspend fun sendContactRequest(senderUserId: String, receiverUserId: String)

    suspend fun getPendingRequests(currentUserId: String): List<ContactRequest>

    suspend fun getSentRequests(currentUserId: String): List<ContactRequest>

    suspend fun withdrawContactRequest(requestId: String)

}