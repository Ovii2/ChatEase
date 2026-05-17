package com.example.chatease.domain.model

data class Message(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timeStamp: Long = 0L,
    val seenBy: List<String> = emptyList()
)
