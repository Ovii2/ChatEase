package com.example.chatease.data.remote.dto

data class FileAttachmentDto(
    val id: String = "",
    val name: String = "",
    val size: Long = 0L,
    val url: String = "",
    val mimeType: String = ""
)
