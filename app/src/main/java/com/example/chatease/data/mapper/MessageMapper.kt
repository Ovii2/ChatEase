package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.MessageDto
import com.example.chatease.domain.model.Message

fun MessageDto.toDomain(): Message {
    return Message(
        messageId = messageId,
        conversationId = conversationId,
        senderId = senderId,
        text = text,
        timeStamp = timeStamp,
        seenBy = seenBy,
        reactions = reactions,
        messageType = messageType,
        replyMessage = replyMessage,
        fileAttachments = fileAttachments.map { it.toDomain() }
    )
}

fun Message.toDto(): MessageDto {
    return MessageDto(
        messageId = messageId,
        conversationId = conversationId,
        senderId = senderId,
        text = text,
        timeStamp = timeStamp,
        seenBy = seenBy,
        reactions = reactions,
        messageType = messageType,
        replyMessage = replyMessage,
        fileAttachments = fileAttachments.map { it.toDto() }
    )
}