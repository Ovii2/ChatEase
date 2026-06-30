package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.ConversationDto
import com.example.chatease.domain.model.Conversation

fun ConversationDto.toDomain(): Conversation {
    return Conversation(
        id = id,
        creatorId = creatorId,
        participantIds = participantIds,
        typingUserIds = typingUserIds,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCounts = unreadCounts,
        deletedFor = deletedFor
    )
}

fun Conversation.toDto(): ConversationDto {
    return ConversationDto(
        id = id,
        creatorId = creatorId,
        participantIds = participantIds,
        typingUserIds = typingUserIds,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCounts = unreadCounts,
        deletedFor = deletedFor
    )
}