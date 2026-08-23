package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MessageType

data class ConversationDto(
    val id: String = "",
    val type: ConversationType = ConversationType.DIRECT,
    val creatorId: String = "",
    val participantIds: List<String> = emptyList(),
    val typingUserIds: List<String> = emptyList(),
    val typingTexts: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCounts: Map<String, Int> = emptyMap(),
    val deletedFor: List<String> = emptyList(),
    val lastMessageType: MessageType = MessageType.TEXT,
    val lastMessageSenderId: String = ""
)
