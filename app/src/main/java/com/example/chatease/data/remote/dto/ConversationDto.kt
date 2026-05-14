package com.example.chatease.data.remote.dto

import com.example.chatease.domain.model.User

data class ConversationDto(
    val id: String = "",
    val participants: List<User> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val unreadCount: Int = 0
)
