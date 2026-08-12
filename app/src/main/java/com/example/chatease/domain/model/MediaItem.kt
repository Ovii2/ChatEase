package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.MediaType

data class MediaItem(
    val id: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val type: MediaType,
    val fileName: String = "",
    val fileSize: Long = 0L,
    val mimeType: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timeStamp: Long = 0L
)
