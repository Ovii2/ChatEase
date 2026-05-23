package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.ContactRequestCooldownDto
import com.example.chatease.domain.model.ContactRequestCooldown

fun ContactRequestCooldownDto.toDomain(): ContactRequestCooldown {
    return ContactRequestCooldown(
        id = id,
        senderUserId = senderUserId,
        receiverUserId = receiverUserId,
        withdrawnAt = withdrawnAt,
        expiresAt = expiresAt,
        timeLeft = timeLeft
    )
}

fun ContactRequestCooldown.toDto(): ContactRequestCooldownDto {
    return ContactRequestCooldownDto(
        id = id,
        senderUserId = senderUserId,
        receiverUserId = receiverUserId,
        withdrawnAt = withdrawnAt,
        expiresAt = expiresAt,
        timeLeft = timeLeft
    )
}