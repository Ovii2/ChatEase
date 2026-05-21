package com.example.chatease.data.repository

import com.example.chatease.data.remote.dto.ContactRequestDto
import com.example.chatease.domain.repository.ContactRequestRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRequestRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ContactRequestRepository {

    companion object {
        const val CONTACT_REQUESTS = "contact_requests"
    }

    override suspend fun sendContactRequest(
        senderUserId: String,
        receiverUserId: String
    ) {
        val requestId = firestore
            .collection(CONTACT_REQUESTS)
            .document()
            .id

        val contactRequest = ContactRequestDto(
            id = requestId,
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            timestamp = System.currentTimeMillis()
        )

        firestore
            .collection(CONTACT_REQUESTS)
            .document(requestId)
            .set(contactRequest)
            .await()
    }
}