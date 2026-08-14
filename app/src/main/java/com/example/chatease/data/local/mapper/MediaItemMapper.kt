package com.example.chatease.data.local.mapper

import com.example.chatease.data.local.entity.MediaItemEntity
import com.example.chatease.domain.model.MediaItem

fun MediaItemEntity.toDomain(): MediaItem {
    return MediaItem(
        id = id,
        thumbnailUrl = thumbnailUrl,
        mediaUrl = mediaUrl,
        type = type,
        fileName = fileName,
        fileSize = fileSize,
        mimeType = mimeType,
        senderId = senderId,
        senderName = senderName,
        timeStamp = timeStamp
    )
}

fun MediaItem.toEntity(conversationId: String): MediaItemEntity {
    return MediaItemEntity(
        id = id,
        conversationId = conversationId,
        thumbnailUrl = thumbnailUrl,
        mediaUrl = mediaUrl,
        type = type,
        fileName = fileName,
        fileSize = fileSize,
        mimeType = mimeType,
        senderId = senderId,
        senderName = senderName,
        timeStamp = timeStamp
    )
}