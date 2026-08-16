package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.MessageType

data class Message(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timeStamp: Long = 0L,
    val seenBy: List<String> = emptyList(),
    val reactions: Map<String, String> = emptyMap(),
    val messageType: MessageType = MessageType.TEXT,
    val replyMessage: ReplyMessage? = null,
    val fileAttachments: List<FileAttachment> = emptyList()
)
