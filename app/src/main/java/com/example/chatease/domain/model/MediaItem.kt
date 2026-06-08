package com.example.chatease.domain.model

import com.example.chatease.domain.model.enums.MediaType

data class MediaItem(
    val id: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val type: MediaType
)
