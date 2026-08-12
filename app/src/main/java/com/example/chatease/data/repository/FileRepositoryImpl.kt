package com.example.chatease.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.domain.repository.FileRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
        senderId: String,
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
            .child(fileName)

        val metadataData = storageMetadata {
            contentType = mimeType
            setCustomMetadata("senderId", senderId)
        }

        val uploadTask = fileReference.putFile(fileUri, metadataData)

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

    override suspend fun saveFileToDownloads(
        fileUrl: String,
        fileName: String,
        mimeType: String
    ) {
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw IllegalStateException("Failed to create download file")

                try {
                    context.contentResolver
                        .openOutputStream(uri)
                        ?.use { outputStream ->
                            storage
                                .getReferenceFromUrl(fileUrl)
                                .stream
                                .await()
                                .stream
                                .use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                        }

                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)

                    context.contentResolver.update(
                        uri,
                        contentValues,
                        null,
                        null
                    )
                } catch (e: Exception) {
                    context.contentResolver.delete(uri, null, null)
                    throw e
                }
            } else {
                val downloadsDirectory =
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )

                val localFile = File(
                    downloadsDirectory,
                    fileName
                )

                storage
                    .getReferenceFromUrl(fileUrl)
                    .getFile(localFile)
                    .await()
            }
        }
    }

    override suspend fun getMediaItems(conversationId: String): List<MediaItem> {
        val result = storage.reference
            .child(CHAT_FILES)
            .child(conversationId)
            .listAll()
            .await()

        return result.items.map { fileReference ->
            val metadata = fileReference.metadata.await()
            val downloadUrl = fileReference.downloadUrl.await()
            val senderId = metadata.getCustomMetadata("senderId").orEmpty()

            val mediaType = when {
                metadata.contentType?.startsWith("image/") == true -> MediaType.IMAGE
                metadata.contentType?.startsWith("video/") == true -> MediaType.VIDEO
                else -> MediaType.FILE
            }

            MediaItem(
                id = fileReference.name,
                thumbnailUrl = "",
                mediaUrl = downloadUrl.toString(),
                type = mediaType,
                fileName = fileReference.name,
                fileSize = metadata.sizeBytes,
                mimeType = metadata.contentType.orEmpty(),
                senderId = senderId,
                senderName = "",
                timeStamp = metadata.creationTimeMillis
            )
        }
    }

}