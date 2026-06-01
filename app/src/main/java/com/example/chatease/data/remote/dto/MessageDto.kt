package com.example.chatease.data.remote.dto

data class MessageDto(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timeStamp: Long = 0L,
    val seenBy: List<String> = emptyList(),
    val reactions: Map<String, String> = emptyMap()
)
