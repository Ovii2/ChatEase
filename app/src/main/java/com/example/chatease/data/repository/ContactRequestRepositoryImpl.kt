package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ContactRequestDto
import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.repository.ContactRequestRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ContactRequestRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ContactRequestRepository {

    companion object {
        const val CONTACT_REQUESTS = "contact_requests"
        const val RECEIVER_USER_ID = "receiverUserId"
        const val SENDER_USER_ID = "senderUserId"
        const val STATUS = "status"
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

    override suspend fun getPendingRequests(currentUserId: String): List<ContactRequest> {
        val snapshot = firestore
            .collection(CONTACT_REQUESTS)
            .whereEqualTo(RECEIVER_USER_ID, currentUserId)
            .whereEqualTo(STATUS, ContactRequestStatus.PENDING)
            .get()
            .await()

        return mapDocumentsToContactRequests(snapshot)
    }

    override suspend fun getSentRequests(currentUserId: String): List<ContactRequest> {
        val snapshot = firestore
            .collection(CONTACT_REQUESTS)
            .whereEqualTo(SENDER_USER_ID, currentUserId)
            .whereEqualTo(STATUS, ContactRequestStatus.PENDING)
            .get()
            .await()

        return mapDocumentsToContactRequests(snapshot)
    }

    override suspend fun withdrawContactRequest(requestId: String) {
        firestore
            .collection(CONTACT_REQUESTS)
            .document(requestId)
            .delete()
            .await()
    }

    private fun mapDocumentsToContactRequests(snapshot: QuerySnapshot): List<ContactRequest> {
        return snapshot.documents.mapNotNull { document ->
            document.toObject(ContactRequestDto::class.java)?.toDomain()
        }
    }
}