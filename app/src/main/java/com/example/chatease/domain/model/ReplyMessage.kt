package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.MessageType

data class ReplyMessage(
    val messageId: String = "",
    val senderId: String = "",
    val text: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val fileName: String = "",
    val imageUrl: String? = null,
    val imageCount: Int = 0
)
