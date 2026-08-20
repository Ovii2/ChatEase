package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.ReplyMessage
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.FileDownloadUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isConversationDeleted = MutableStateFlow(false)
    val isConversationDeleted = _isConversationDeleted.asStateFlow()

    private val _isConversationCreator = MutableStateFlow(false)
    val isConversationCreator = _isConversationCreator.asStateFlow()

    private val _typingUserIds = MutableStateFlow<List<String>>(emptyList())
    val typingUserIds = _typingUserIds.asStateFlow()

    private val _isBlockedByOtherUser = MutableStateFlow(false)
    val isBlockedByOtherUser = _isBlockedByOtherUser.asStateFlow()

    private val _fileUploadProgress = MutableStateFlow<Float?>(null)
    val fileUploadProgress = _fileUploadProgress.asStateFlow()

    private val _uploadingFileId = MutableStateFlow<String?>(null)
    val uploadingFileId = _uploadingFileId.asStateFlow()

    private val _pendingFileMessage = MutableStateFlow<Message?>(null)
    val pendingFileMessage = _pendingFileMessage.asStateFlow()

    private val _openingFileMessageId = MutableStateFlow<String?>(null)
    val openingFileMessageId = _openingFileMessageId.asStateFlow()

    private val _fileDownloadUiState = MutableStateFlow(FileDownloadUiState())
    val fileDownloadUiState = _fileDownloadUiState.asStateFlow()

    private val _pendingImageMessage = MutableStateFlow<Message?>(null)
    val pendingImageMessage = _pendingImageMessage.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    val firstUnreadMessageId: String?
        get() = _messages.value.firstOrNull { message ->
            currentUserId !in message.seenBy && message.senderId != currentUserId
        }?.messageId

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                val conversation = conversationRepository.getConversation(conversationId)
                val loggedInUserId = auth.currentUser?.uid ?: return@launch
                loadCurrentUser(loggedInUserId)
                _isConversationCreator.value = conversation.creatorId == loggedInUserId
                val otherUserId = conversation.participantIds.first {
                    it != loggedInUserId
                }
                observeIsBlockedByOtherUser(otherUserId)
                observeUser(otherUserId)
                observeMessages(conversationId)
                observeConversation(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to load conversation")
            }
        }
    }

    fun sendMessage(conversationId: String, text: String, repliedMessage: Message?) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val message = Message(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    text = text.trim(),
                    timeStamp = System.currentTimeMillis(),
                    seenBy = listOf(currentUserId),
                    messageType = MessageType.TEXT,
                    replyMessage = repliedMessage?.let { message ->
                        ReplyMessage(
                            messageId = message.messageId,
                            senderId = message.senderId,
                            text = message.text,
                            messageType = message.messageType,
                            fileName = message.fileAttachments.firstOrNull()?.name.orEmpty(),
                            imageUrl = message.fileAttachments.firstOrNull()?.url,
                            imageCount = message.fileAttachments.size
                        )
                    }
                )
                conversationRepository.sendMessage(message)
                conversationRepository.updateTypingStatus(conversationId, currentUserId, false)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to send message")
            }
        }
    }

    fun markMessagesAsSeen(conversationId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                conversationRepository.markMessagesAsSeen(conversationId, currentUserId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to mark message as seen")
            }
        }
    }

    fun addReactionToMessage(conversationId: String, messageId: String, reaction: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                conversationRepository.addReactionToMessage(
                    conversationId = conversationId,
                    messageId = messageId,
                    userId = currentUserId,
                    reaction = reaction
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to add reaction to message")
            }
        }
    }

    fun removeReactionFromMessage(conversationId: String, messageId: String) {
        viewModelScope.launch {
            try {
                conversationRepository.removeReactionFromMessage(
                    conversationId = conversationId,
                    messageId = messageId,
                    userId = currentUserId
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to add remove to message")
            }
        }
    }

    fun deleteConversationIfEmpty(conversationId: String) {
        viewModelScope.launch {
            try {
                conversationRepository.deleteIfEmptyConversation(conversationId)
                _isConversationDeleted.value = true
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to delete conversation")
            }
        }
    }

    fun updateTypingStatus(conversationId: String, isTyping: Boolean) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                conversationRepository.updateTypingStatus(
                    conversationId = conversationId,
                    userId = userId,
                    isTyping = isTyping
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", e.message ?: "Failed to update typing status")
            }
        }
    }

    fun checkIfUserIsBlockedByOtherUser(otherUserId: String) {
        viewModelScope.launch {
            try {
                _isBlockedByOtherUser.value = userRepository.isBlockedByUser(otherUserId)
            } catch (e: Exception) {
                Log.v(
                    "ChatViewModel",
                    e.message ?: "Failed to check if user is blocked by other user"
                )
            }
        }
    }

    fun sendFile(conversationId: String, fileUri: Uri, currentUserId: String) {
        val fileId = System.currentTimeMillis().toString()
        viewModelScope.launch {
            _fileUploadProgress.value = 0f

            try {
                _pendingFileMessage.value = createPendingFileMessage(
                    conversationId = conversationId,
                    fileUri = fileUri,
                    fileId = fileId
                )
                val fileAttachment = fileRepository.uploadFile(
                    conversationId = conversationId,
                    fileUri = fileUri,
                    fileId = fileId,
                    senderId = currentUserId,
                    onFileReady = { fileAttachment ->
                        _pendingFileMessage.value = _pendingFileMessage.value?.copy(
                            fileAttachments = listOf(fileAttachment)
                        )
                    },
                    onProgress = { uploadingFileId, progress ->
                        _uploadingFileId.value = uploadingFileId
                        _fileUploadProgress.value = progress
                    },
                )

                val message = Message(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    timeStamp = System.currentTimeMillis(),
                    messageType = MessageType.FILE,
                    fileAttachments = listOf(fileAttachment)
                )

                conversationRepository.sendMessage(message)
                preLoadMediaItems(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to send file", e)
            } finally {
                _uploadingFileId.value = null
                _fileUploadProgress.value = null
                _pendingFileMessage.value = null
            }
        }
    }

    fun openFile(messageId: String, fileUrl: String, fileName: String, onFileReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _openingFileMessageId.value = messageId
            try {
                val uri = fileRepository.downloadFile(
                    fileUrl = fileUrl,
                    fileName = fileName
                )

                onFileReady(uri)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to open file", e)
            } finally {
                _openingFileMessageId.value = null
            }
        }
    }

    fun downloadFile(
        messageId: String,
        fileUrl: String,
        fileName: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            _fileDownloadUiState.value = FileDownloadUiState(
                messageId = messageId,
                state = FileDownloadState.DOWNLOADING
            )
            try {
                fileRepository.saveFileToDownloads(
                    fileUrl = fileUrl,
                    fileName = fileName,
                    mimeType = mimeType
                )
                _fileDownloadUiState.value = FileDownloadUiState(
                    messageId = messageId,
                    state = FileDownloadState.SUCCESS
                )
            } catch (e: Exception) {
                _fileDownloadUiState.value = FileDownloadUiState(
                    messageId = messageId,
                    state = FileDownloadState.FAILED
                )
                Log.e("ChatViewModel", "Failed to download file", e)
            }
        }
    }

    fun sendImages(
        conversationId: String,
        imageUris: List<Uri>,
        currentUserId: String
    ) {
        if (imageUris.isEmpty() || imageUris.size > 10) return

        viewModelScope.launch {
            try {
                val pendingAttachments = imageUris.map {
                    FileAttachment(
                        id = UUID.randomUUID().toString(),
                        url = "",
                        mimeType = "image/*"
                    )
                }
                _pendingImageMessage.value = Message(
                    messageId = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    senderId = currentUserId,
                    timeStamp = System.currentTimeMillis(),
                    messageType = MessageType.IMAGE,
                    fileAttachments = pendingAttachments
                )

                val attachments = imageUris.map { imageUri ->
                    fileRepository.uploadFile(
                        conversationId = conversationId,
                        fileUri = imageUri,
                        fileId = UUID.randomUUID().toString(),
                        senderId = currentUserId,
                        onFileReady = {},
                        onProgress = { _, _ -> }
                    )
                }

                val message = Message(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    timeStamp = System.currentTimeMillis(),
                    messageType = MessageType.IMAGE,
                    fileAttachments = attachments
                )

                conversationRepository.sendMessage(message)
                preLoadMediaItems(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to send images", e)
            } finally {
                _pendingImageMessage.value = null
            }
        }
    }

    fun loadCurrentUser(loggedInUserId: String) {
        viewModelScope.launch {
            try {
                userRepository.observeUser(loggedInUserId)
                    .collect { user ->
                        _currentUser.value = user
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to load current user", e)
            }
        }
    }

    fun preLoadMediaItems(conversationId: String) {
        viewModelScope.launch {
            try {
                fileRepository.refreshMediaItems(conversationId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to pre-load media items", e)
            }
        }
    }

    private fun createPendingFileMessage(
        conversationId: String,
        fileUri: Uri,
        fileId: String
    ): Message {
        return Message(
            messageId = fileId,
            conversationId = conversationId,
            senderId = currentUserId,
            timeStamp = System.currentTimeMillis(),
            messageType = MessageType.FILE,
            fileAttachments = listOf(
                FileAttachment(
                    id = fileId,
                    name = fileUri.lastPathSegment.orEmpty()
                )
            )
        )
    }

    private fun observeIsBlockedByOtherUser(otherUserId: String) {
        viewModelScope.launch {
            userRepository.observeIsBlockedByUser(otherUserId)
                .collect { isBlocked ->
                    _isBlockedByOtherUser.value = isBlocked
                }
        }
    }

    private fun observeConversation(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.observeConversation(conversationId)
                .collect { conversation ->
                    if (conversation == null) {
                        _isConversationDeleted.value = true
                        return@collect
                    }

                    _typingUserIds.value = conversation.typingUserIds.filter { userId ->
                        userId != currentUserId
                    }
                }
        }
    }

    private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.observeMessages(conversationId)
                .collect { messages ->
                    _messages.value = messages
                }
        }
    }

    private fun observeUser(userId: String) {
        viewModelScope.launch {
            userRepository.observeUser(userId)
                .collect { user ->
                    _user.value = user
                }
        }
    }

}