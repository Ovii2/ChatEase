package com.example.chatease.data.remote.dto

data class ContactRequestCooldownDto(
    val id: String = "",
    val senderUserId: String = "",
    val receiverUserId: String = "",
    val withdrawnAt: Long = 0L,
    val expiresAt: Long = 0L,
    val timeLeft: Long = 0L
)
