package com.example.chatease.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.repository.FileRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
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
        fileUri: Uri,
        fileId: String,
        onFileReady: (FileAttachment) -> Unit,
        onProgress: (String, Float) -> Unit
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

        onFileReady(
            FileAttachment(
                id = fileId,
                name = fileName,
                size = fileSize,
                mimeType = mimeType
            )
        )

        val fileReference = storage.reference
            .child(CHAT_FILES)
            .child(conversationId)
            .child(fileId)


        val uploadTask = fileReference.putFile(fileUri)

        uploadTask.addOnProgressListener { snapshot ->
            val progress = snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount.toFloat()

            onProgress(fileId, progress)
        }

        uploadTask.await()

        val downloadUrl = fileReference
            .downloadUrl
            .await()
            .toString()

        return FileAttachment(
            id = fileId,
            name = fileName,
            size = fileSize,
            url = downloadUrl,
            mimeType = mimeType
        )
    }

    override suspend fun downloadFile(fileUrl: String, fileName: String): Uri {
        val localFile = File(
            context.cacheDir,
            fileName
        )

        storage
            .getReferenceFromUrl(fileUrl)
            .getFile(localFile)
            .await()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            localFile
        )
    }
}