package com.example.chatease.domain.model

data class Conversation(
    val id: String = "",
    val participants: List<User> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCount: Int = 0
)
