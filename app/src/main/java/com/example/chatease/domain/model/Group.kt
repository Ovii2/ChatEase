package com.example.chatease.domain.model

data class Group(
    val conversationId: String = "",
    val userIds: List<String> = emptyList(),
    val adminIds: List<String> = emptyList(),
    val ownerId: String = "",
    val name: String = "",
    val imageUrl: String? = null
)
