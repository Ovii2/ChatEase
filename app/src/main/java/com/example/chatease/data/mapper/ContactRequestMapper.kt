package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.ContactRequestDto
import com.example.chatease.domain.model.ContactRequest

fun ContactRequestDto.toDomain(): ContactRequest {
    return ContactRequest(
        id = id,
        senderUserId = senderUserId,
        receiverUserId = receiverUserId,
        timestamp = timestamp,
        status = status
    )
}

fun ContactRequest.toDto(): ContactRequestDto {
    return ContactRequestDto(
        id = id,
        senderUserId = senderUserId,
        receiverUserId = receiverUserId,
        timestamp = timestamp,
        status = status
    )
}