package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.ContactRequestStatus

data class ContactRequest(
    val id: String = "",
    val senderUserId: String = "",
    val receiverUserId: String = "",
    val timestamp: Long = 0L,
    val status: ContactRequestStatus = ContactRequestStatus.PENDING
)
