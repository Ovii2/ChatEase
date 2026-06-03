package com.example.chatease.domain.model

data class Conversation(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCounts: Map<String, Int> = emptyMap()
)
