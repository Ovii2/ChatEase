package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Contact

interface ContactsRepository {

    suspend fun getContacts(currentUserId: String): List<Contact>

}