package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.enums.ContactRequestStatus

data class ContactRequestDto(
    val id: String = "",
    val senderUserId: String = "",
    val receiverUserId: String = "",
    val timestamp: Long = 0L,
    val status: ContactRequestStatus = ContactRequestStatus.PENDING
)
