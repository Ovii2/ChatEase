package com.example.chatease.domain.repository

interface ContactRequestRepository {

    suspend fun sendContactRequest(
        senderUserId: String,
        receiverUserId: String
    )
}