package com.example.chatease.domain.repository

import android.net.Uri
import com.example.chatease.domain.model.FileAttachment

interface FileRepository {

    suspend fun uploadFile(conversationId: String, fileUri: Uri): FileAttachment

    suspend fun downloadFile(fileUrl: String, fileName: String): Uri
}