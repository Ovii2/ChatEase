package com.example.chatease.domain.model

data class FileAttachment(
    val name: String = "",
    val size: Long = 0L,
    val url: String = "",
    val mimeType: String = ""
)
