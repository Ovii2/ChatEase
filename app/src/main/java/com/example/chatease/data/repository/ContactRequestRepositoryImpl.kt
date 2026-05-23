package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ContactRequestCooldownDto
import com.example.chatease.data.remote.dto.ContactRequestDto
import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.ContactRequestCooldown
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
        const val CONTACT_REQUEST_COOLDOWNS = "contact_request_cooldowns"
        const val RECEIVER_USER_ID = "receiverUserId"
        const val SENDER_USER_ID = "senderUserId"
        const val STATUS = "status"
        const val HOURS_24_IN_MILLIS = 24 * 60 * 60 * 1000L
    }

    override suspend fun sendContactRequest(
        senderUserId: String,
        receiverUserId: String
    ) {
        if (isCooldownActive(senderUserId, receiverUserId)) {
            throw Exception("Cooldown active")
        }

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

    override suspend fun withdrawContactRequest(
        requestId: String,
        senderUserId: String,
        receiverUserId: String
    ) {
        createCooldown(senderUserId, receiverUserId)

        firestore
            .collection(CONTACT_REQUESTS)
            .document(requestId)
            .delete()
            .await()
    }

    override suspend fun createCooldown(
        senderUserId: String,
        receiverUserId: String
    ) {
        val withdrawnAt = System.currentTimeMillis()
        val expiresAt = withdrawnAt + HOURS_24_IN_MILLIS
        val timeLeft = expiresAt - withdrawnAt

        val cooldown = ContactRequestCooldownDto(
            id = "${senderUserId}_${receiverUserId}",
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            withdrawnAt = withdrawnAt,
            expiresAt = expiresAt,
            timeLeft = timeLeft
        )

        firestore
            .collection(CONTACT_REQUEST_COOLDOWNS)
            .document(cooldown.id)
            .set(cooldown)
            .await()
    }

    override suspend fun isCooldownActive(
        senderUserId: String,
        receiverUserId: String
    ): Boolean {
        val cooldownId = "${senderUserId}_${receiverUserId}"

        val document = firestore
            .collection(CONTACT_REQUEST_COOLDOWNS)
            .document(cooldownId)
            .get()
            .await()

        val cooldown = document.toObject(ContactRequestCooldownDto::class.java)

        return document.exists() && (cooldown?.expiresAt ?: 0L) > System.currentTimeMillis()
    }

    override suspend fun getCooldown(
        senderUserId: String,
        receiverUserId: String
    ): ContactRequestCooldown? {
        val cooldownId = "${senderUserId}_${receiverUserId}"

        val document = firestore
            .collection(CONTACT_REQUEST_COOLDOWNS)
            .document(cooldownId)
            .get()
            .await()

        val cooldown = document.toObject(ContactRequestCooldownDto::class.java)

        if (cooldown != null && cooldown.expiresAt <= System.currentTimeMillis()) {
            document.reference.delete().await()
            return null
        }

        return document.toObject(ContactRequestCooldownDto::class.java)?.toDomain()
    }

    private fun mapDocumentsToContactRequests(snapshot: QuerySnapshot): List<ContactRequest> {
        return snapshot.documents.mapNotNull { document ->
            document.toObject(ContactRequestDto::class.java)?.toDomain()
        }
    }
}