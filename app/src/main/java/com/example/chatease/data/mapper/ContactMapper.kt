package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.ContactDto
import com.example.chatease.domain.model.Contact

fun ContactDto.toDomain(): Contact {
    return Contact(
        id = id,
        userIds = userIds,
        createdAt = createdAt
    )
}

fun Contact.toDto(): ContactDto {
    return ContactDto(
        id = id,
        userIds = userIds,
        createdAt = createdAt
    )
}