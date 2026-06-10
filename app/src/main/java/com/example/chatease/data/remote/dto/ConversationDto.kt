package com.example.chatease.data.remote.dto

data class ConversationDto(
    val id: String = "",
    val creatorId: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCounts: Map<String, Int> = emptyMap()
)
