package com.example.chatease.data.remote.dto

data class ConversationDto(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCount: Int = 0
)
