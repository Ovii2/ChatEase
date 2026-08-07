package com.example.chatease.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.repository.FileRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) : FileRepository {

    companion object {
        private const val CHAT_FILES = "chat_files"
    }

    override suspend fun uploadFile(
        conversationId: String,
        fileUri: Uri
    ): FileAttachment {
        val contentResolver = context.contentResolver

        var fileName = System.currentTimeMillis().toString()
        var fileSize = 0L

        contentResolver.query(
            fileUri,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            if (cursor.moveToFirst()) {
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }

                if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }

        val mimeType = contentResolver.getType(fileUri).orEmpty()

        val fileReference = storage.reference
            .child(CHAT_FILES)
            .child(conversationId)
            .child("${System.currentTimeMillis()}_$fileName")

        fileReference
            .putFile(fileUri)
            .await()

        val downloadUrl = fileReference
            .downloadUrl
            .await()
            .toString()

        return FileAttachment(
            name = fileName,
            size = fileSize,
            url = downloadUrl,
            mimeType = mimeType
        )
    }
}