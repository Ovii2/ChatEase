package com.example.chatease.domain.repository

import android.net.Uri
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.MediaItem

interface FileRepository {

    suspend fun uploadFile(
        conversationId: String,
        fileUri: Uri,
        fileId: String,
        senderId: String,
        onFileReady: (FileAttachment) -> Unit,
        onProgress: (String, Float) -> Unit
    ): FileAttachment

    suspend fun downloadFile(fileUrl: String, fileName: String): Uri

    suspend fun saveFileToDownloads(
        fileUrl: String,
        fileName: String,
        mimeType: String
    )

    suspend fun getMediaItems(conversationId: String): List<MediaItem>
}