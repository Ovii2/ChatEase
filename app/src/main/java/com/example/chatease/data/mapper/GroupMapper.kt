package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.GroupDto
import com.example.chatease.domain.model.Group

fun GroupDto.toDomain(): Group {
    return Group(
        conversationId = conversationId,
        userIds = userIds,
        adminIds = adminIds,
        visibleToUserIds = visibleToUserIds,
        ownerId = ownerId,
        name = name,
        imageUrl = imageUrl,
        removedAtByUserId = removedAtByUserId
    )
}

fun Group.toDto(): GroupDto {
    return GroupDto(
        conversationId = conversationId,
        userIds = userIds,
        adminIds = adminIds,
        visibleToUserIds = visibleToUserIds,
        ownerId = ownerId,
        name = name,
        imageUrl = imageUrl,
        removedAtByUserId = removedAtByUserId
    )
}