package com.example.chatease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chatease.domain.model.enums.MediaType

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val type: MediaType,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val senderId: String,
    val senderName: String,
    val timeStamp: Long
)
