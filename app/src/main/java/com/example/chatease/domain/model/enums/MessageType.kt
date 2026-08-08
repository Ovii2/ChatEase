package com.example.chatease.domain.model.enums

enum class MessageType {
    TEXT,
    FILE,
    IMAGE,
    GIF,
    AUDIO,
    VIDEO
}

fun MessageType.toScreenName(): String {
    return when (this) {
        MessageType.TEXT -> "Text"
        MessageType.FILE -> "File"
        MessageType.IMAGE -> "Image"
        MessageType.GIF -> "Gif"
        MessageType.AUDIO -> "Audio"
        MessageType.VIDEO -> "Video"
    }
}