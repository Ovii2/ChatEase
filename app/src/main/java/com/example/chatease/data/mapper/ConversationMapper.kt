package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.ConversationDto
import com.example.chatease.domain.model.Conversation

fun ConversationDto.toDomain(): Conversation {
    return Conversation(
        id = id,
        participants = participants,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount
    )
}

fun Conversation.toDto(): ConversationDto {
    return ConversationDto(
        id = id,
        participants = participants,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount
    )
}