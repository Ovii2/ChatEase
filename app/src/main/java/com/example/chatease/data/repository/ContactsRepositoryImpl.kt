package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ContactDto
import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.repository.ContactsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactsRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ContactsRepository {

    companion object {
        const val CONTACTS = "contacts"
        const val USER_IDS = "userIds"
    }

    override suspend fun getContacts(currentUserId: String): List<Contact> {
        val snapshot = firestore
            .collection(CONTACTS)
            .whereArrayContains(USER_IDS, currentUserId)
            .get()
            .await()

        return snapshot.toObjects(ContactDto::class.java)
            .map { it.toDomain() }
    }
}