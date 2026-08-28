package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.FileAttachment
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val userRepository: UserRepository = mock()
    private val fileRepository: FileRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: ChatViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val CONVERSATION_ID = "conversation_1"
        private const val MESSAGE_ID = "message_1"
        private const val REQUEST_TEXT = "Hello"
        private const val REACTION = "❤️"

        private const val FILE_ID = "file_1"
        private const val FILE_NAME = "test.pdf"
        private const val FILE_URL = "https://test.com/test.pdf"
        private const val MIME_TYPE = "application/pdf"
    }

    @Before
    fun setUp() {
        viewModel = ChatViewModel(
            auth = auth,
            conversationRepository = conversationRepository,
            userRepository = userRepository,
            fileRepository = fileRepository
        )
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun setupUser(userId: String): User {
        return User(
            uid = userId
        )
    }

    private fun setupMessage(
        messageId: String = MESSAGE_ID,
        senderId: String = USER_2_ID,
        seenBy: List<String> = emptyList(),
        messageType: MessageType = MessageType.TEXT,
        fileAttachments: List<FileAttachment> = emptyList()
    ): Message {
        return Message(
            messageId = messageId,
            conversationId = CONVERSATION_ID,
            senderId = senderId,
            text = REQUEST_TEXT,
            seenBy = seenBy,
            messageType = messageType,
            fileAttachments = fileAttachments
        )
    }

    private suspend fun stubLoadConversation(
        creatorId: String = USER_1_ID,
        messages: List<Message> = emptyList(),
        isBlocked: Boolean = true,
        deletedConversation: Boolean = false
    ): Conversation {
        stubFirebaseUser()

        val conversation = mock<Conversation>()

        whenever(conversation.creatorId).thenReturn(creatorId)
        whenever(conversation.participantIds).thenReturn(listOf(USER_1_ID, USER_2_ID))
        whenever(conversation.typingUserIds).thenReturn(listOf(USER_1_ID, USER_2_ID))
        whenever(conversation.typingTexts)
            .thenReturn(
                mapOf(
                    USER_2_ID to "typing..."
                )
            )

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(conversation)

        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(setupUser(USER_1_ID)))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        whenever(userRepository.observeIsBlockedByUser(USER_2_ID))
            .thenReturn(flowOf(isBlocked))

        whenever(conversationRepository.observeMessages(CONVERSATION_ID))
            .thenReturn(flowOf(messages))

        if (deletedConversation) {
            whenever(conversationRepository.observeConversation(CONVERSATION_ID))
                .thenReturn(flowOf(null))
        } else {
            whenever(conversationRepository.observeConversation(CONVERSATION_ID))
                .thenReturn(flowOf(conversation))
        }

        return conversation
    }


    @Test
    fun `should return current user id`() {
        stubFirebaseUser()

        assertEquals(USER_1_ID, viewModel.currentUserId)
    }

    @Test
    fun `should return empty current user id when user is null`() {
        whenever(auth.currentUser).thenReturn(null)

        assertEquals("", viewModel.currentUserId)
    }

    @Test
    fun `should load conversation`() = runTest {
        val seenMessage = setupMessage(
            messageId = "seen",
            senderId = USER_2_ID,
            seenBy = listOf(USER_1_ID)
        )

        val ownMessage = setupMessage(
            messageId = "own",
            senderId = USER_1_ID,
            seenBy = emptyList()
        )

        val unreadMessage = setupMessage(
            messageId = MESSAGE_ID,
            senderId = USER_2_ID,
            seenBy = emptyList()
        )

        stubLoadConversation(
            messages = listOf(
                seenMessage,
                ownMessage,
                unreadMessage
            )
        )

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()

        assertEquals(setupUser(USER_2_ID), viewModel.user.value)
        assertEquals(setupUser(USER_1_ID), viewModel.currentUser.value)

        assertEquals(
            listOf(
                seenMessage,
                ownMessage,
                unreadMessage
            ),
            viewModel.messages.value
        )

        assertTrue(viewModel.isConversationCreator.value)
        assertTrue(viewModel.isBlockedByOtherUser.value)

        assertEquals(
            listOf(USER_2_ID),
            viewModel.typingUserIds.value
        )

        assertEquals(
            mapOf(USER_2_ID to "typing..."),
            viewModel.typingTexts.value
        )

        assertEquals(
            MESSAGE_ID,
            viewModel.firstUnreadMessageId
        )
    }

    @Test
    fun `should return null when there are no unread messages`() = runTest {
        val seenMessage = setupMessage(
            messageId = "seen",
            senderId = USER_2_ID,
            seenBy = listOf(USER_1_ID)
        )

        val ownMessage = setupMessage(
            messageId = "own",
            senderId = USER_1_ID,
            seenBy = emptyList()
        )

        stubLoadConversation(
            messages = listOf(
                seenMessage,
                ownMessage
            )
        )

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()

        assertNull(viewModel.firstUnreadMessageId)
    }

    @Test
    fun `should set conversation creator to false`() = runTest {
        stubLoadConversation(
            creatorId = USER_2_ID
        )

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isConversationCreator.value)
    }

    @Test
    fun `should mark conversation deleted when observed conversation is null`() = runTest {
        stubLoadConversation(
            deletedConversation = true
        )

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isConversationDeleted.value)
    }

    @Test
    fun `should return when loading conversation without current user`() = runTest {
        val conversation = mock<Conversation>()

        whenever(auth.currentUser).thenReturn(null)
        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(conversation)

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository).getConversation(CONVERSATION_ID)
        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should handle exception when loading conversation`() = runTest {
        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.loadConversation(CONVERSATION_ID)
        advanceUntilIdle()

        viewModel.loadConversation(CONVERSATION_ID)
        advanceUntilIdle()

        verify(
            conversationRepository,
            times(2)
        ).getConversation(CONVERSATION_ID)
    }

    @Test
    fun `should send text message`() = runTest {
        stubFirebaseUser()

        viewModel.sendMessage(
            conversationId = CONVERSATION_ID,
            text = "  Hello  ",
            repliedMessage = null
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Message>()

        verify(conversationRepository).sendMessage(captor.capture())

        val message = captor.firstValue

        assertEquals(CONVERSATION_ID, message.conversationId)
        assertEquals(USER_1_ID, message.senderId)
        assertEquals("Hello", message.text)
        assertEquals(MessageType.TEXT, message.messageType)
        assertEquals(listOf(USER_1_ID), message.seenBy)
        assertNull(message.replyMessage)

        verify(conversationRepository)
            .updateTypingStatus(
                conversationId = CONVERSATION_ID,
                userId = USER_1_ID,
                isTyping = false
            )
    }

    @Test
    fun `should send reply without attachment`() = runTest {
        stubFirebaseUser()

        val repliedMessage = setupMessage(
            senderId = USER_2_ID
        )

        viewModel.sendMessage(
            conversationId = CONVERSATION_ID,
            text = REQUEST_TEXT,
            repliedMessage = repliedMessage
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Message>()

        verify(conversationRepository)
            .sendMessage(captor.capture())

        val reply = captor.firstValue.replyMessage!!

        assertEquals(MESSAGE_ID, reply.messageId)
        assertEquals(USER_2_ID, reply.senderId)
        assertEquals(REQUEST_TEXT, reply.text)
        assertEquals(MessageType.TEXT, reply.messageType)
        assertEquals("", reply.fileName)
        assertNull(reply.imageUrl)
        assertEquals(0, reply.imageCount)
    }

    @Test
    fun `should send reply with attachment`() = runTest {
        stubFirebaseUser()

        val attachment = FileAttachment(
            id = FILE_ID,
            name = FILE_NAME,
            url = FILE_URL,
            mimeType = MIME_TYPE
        )

        val repliedMessage = setupMessage(
            senderId = USER_2_ID,
            messageType = MessageType.FILE,
            fileAttachments = listOf(attachment)
        )

        viewModel.sendMessage(
            conversationId = CONVERSATION_ID,
            text = REQUEST_TEXT,
            repliedMessage = repliedMessage
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Message>()

        verify(conversationRepository).sendMessage(captor.capture())

        val reply = captor.firstValue.replyMessage!!

        assertEquals(FILE_NAME, reply.fileName)
        assertEquals(FILE_URL, reply.imageUrl)
        assertEquals(1, reply.imageCount)
    }

    @Test
    fun `should not send message without current user`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.sendMessage(
            conversationId = CONVERSATION_ID,
            text = REQUEST_TEXT,
            repliedMessage = null
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .sendMessage(any())
    }

    @Test
    fun `should handle exception when sending message`() = runTest {
        stubFirebaseUser()

        whenever(conversationRepository.sendMessage(any()))
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.sendMessage(
            CONVERSATION_ID,
            REQUEST_TEXT,
            null
        )
        advanceUntilIdle()

        viewModel.sendMessage(
            CONVERSATION_ID,
            REQUEST_TEXT,
            null
        )
        advanceUntilIdle()

        verify(
            conversationRepository,
            times(2)
        ).sendMessage(any())
    }

    @Test
    fun `should mark messages as seen`() = runTest {
        stubFirebaseUser()

        viewModel.markMessagesAsSeen(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository)
            .markMessagesAsSeen(
                CONVERSATION_ID,
                USER_1_ID
            )
    }

    @Test
    fun `should not mark messages as seen without current user`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.markMessagesAsSeen(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository, never())
            .markMessagesAsSeen(any(), any())
    }

    @Test
    fun `should handle exception when marking messages as seen`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.markMessagesAsSeen(
                CONVERSATION_ID,
                USER_1_ID
            )
        )
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.markMessagesAsSeen(CONVERSATION_ID)
        advanceUntilIdle()

        viewModel.markMessagesAsSeen(CONVERSATION_ID)
        advanceUntilIdle()

        verify(
            conversationRepository,
            times(2)
        ).markMessagesAsSeen(
            CONVERSATION_ID,
            USER_1_ID
        )
    }

    @Test
    fun `should add reaction to message`() = runTest {
        stubFirebaseUser()

        viewModel.addReactionToMessage(
            conversationId = CONVERSATION_ID,
            messageId = MESSAGE_ID,
            reaction = REACTION
        )

        advanceUntilIdle()

        verify(conversationRepository)
            .addReactionToMessage(
                conversationId = CONVERSATION_ID,
                messageId = MESSAGE_ID,
                userId = USER_1_ID,
                reaction = REACTION
            )
    }

    @Test
    fun `should not add reaction without current user`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.addReactionToMessage(
            CONVERSATION_ID,
            MESSAGE_ID,
            REACTION
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .addReactionToMessage(
                any(),
                any(),
                any(),
                any()
            )
    }

    @Test
    fun `should handle exception when adding reaction`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.addReactionToMessage(
                CONVERSATION_ID,
                MESSAGE_ID,
                USER_1_ID,
                REACTION
            )
        )
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.addReactionToMessage(
            CONVERSATION_ID,
            MESSAGE_ID,
            REACTION
        )
        advanceUntilIdle()

        viewModel.addReactionToMessage(
            CONVERSATION_ID,
            MESSAGE_ID,
            REACTION
        )
        advanceUntilIdle()

        verify(
            conversationRepository,
            times(2)
        ).addReactionToMessage(
            CONVERSATION_ID,
            MESSAGE_ID,
            USER_1_ID,
            REACTION
        )
    }

    @Test
    fun `should remove reaction from message`() = runTest {
        stubFirebaseUser()

        viewModel.removeReactionFromMessage(
            conversationId = CONVERSATION_ID,
            messageId = MESSAGE_ID
        )

        advanceUntilIdle()

        verify(conversationRepository)
            .removeReactionFromMessage(
                conversationId = CONVERSATION_ID,
                messageId = MESSAGE_ID,
                userId = USER_1_ID
            )
    }

    @Test
    fun `should handle exception when removing reaction`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.removeReactionFromMessage(
                CONVERSATION_ID,
                MESSAGE_ID,
                USER_1_ID
            )
        )
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.removeReactionFromMessage(
            CONVERSATION_ID,
            MESSAGE_ID
        )
        advanceUntilIdle()

        viewModel.removeReactionFromMessage(
            CONVERSATION_ID,
            MESSAGE_ID
        )
        advanceUntilIdle()

        verify(
            conversationRepository,
            times(2)
        ).removeReactionFromMessage(
            CONVERSATION_ID,
            MESSAGE_ID,
            USER_1_ID
        )
    }

    @Test
    fun `should delete conversation if empty`() = runTest {
        viewModel.deleteConversationIfEmpty(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository).deleteIfEmptyConversation(CONVERSATION_ID)
        assertTrue(viewModel.isConversationDeleted.value)
    }

    @Test
    fun `should handle exception when deleting empty conversation`() = runTest {
        whenever(
            conversationRepository.deleteIfEmptyConversation(CONVERSATION_ID)
        )
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.deleteConversationIfEmpty(CONVERSATION_ID)
        advanceUntilIdle()

        viewModel.deleteConversationIfEmpty(CONVERSATION_ID)
        advanceUntilIdle()

        assertFalse(viewModel.isConversationDeleted.value)

        verify(
            conversationRepository,
            times(2)
        ).deleteIfEmptyConversation(CONVERSATION_ID)
    }

    @Test
    fun `should not update typing status when state has not changed`() = runTest {
        stubFirebaseUser()

        viewModel.updateTypingStatus(
            conversationId = CONVERSATION_ID,
            isTyping = false
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .updateTypingStatus(
                any(),
                any(),
                any()
            )
    }

    @Test
    fun `should update typing status when state changes`() = runTest {
        stubFirebaseUser()

        viewModel.updateTypingStatus(
            CONVERSATION_ID,
            true
        )

        advanceUntilIdle()

        viewModel.updateTypingStatus(
            CONVERSATION_ID,
            false
        )

        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                true
            )

        verify(conversationRepository)
            .updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                false
            )
    }

    @Test
    fun `should not update typing status without current user`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.updateTypingStatus(
            CONVERSATION_ID,
            true
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .updateTypingStatus(
                any(),
                any(),
                any()
            )
    }

    @Test
    fun `should handle exception when updating typing status`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                true
            )
        ).thenThrow(RuntimeException())

        whenever(
            conversationRepository.updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                false
            )
        ).thenThrow(RuntimeException("Failed"))

        viewModel.updateTypingStatus(
            CONVERSATION_ID,
            true
        )
        advanceUntilIdle()

        viewModel.updateTypingStatus(
            CONVERSATION_ID,
            false
        )
        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                true
            )

        verify(conversationRepository)
            .updateTypingStatus(
                CONVERSATION_ID,
                USER_1_ID,
                false
            )
    }

    @Test
    fun `should check if blocked by other user`() = runTest {
        whenever(userRepository.isBlockedByUser(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserIsBlockedByOtherUser(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isBlockedByOtherUser.value)
        verify(userRepository).isBlockedByUser(USER_2_ID)
    }

    @Test
    fun `should handle exception when checking blocked user`() = runTest {
        whenever(userRepository.isBlockedByUser(USER_2_ID))
            .thenThrow(RuntimeException())
            .thenThrow(RuntimeException("Failed"))

        viewModel.checkIfUserIsBlockedByOtherUser(USER_2_ID)
        advanceUntilIdle()

        viewModel.checkIfUserIsBlockedByOtherUser(USER_2_ID)
        advanceUntilIdle()

        verify(
            userRepository,
            times(2)
        ).isBlockedByUser(USER_2_ID)
    }

    @Test
    fun `should send file`() = runTest {
        stubFirebaseUser()

        val fileUri = mock<Uri>()

        whenever(fileUri.lastPathSegment).thenReturn(FILE_NAME)

        val attachment = FileAttachment(
            id = FILE_ID,
            name = FILE_NAME,
            url = FILE_URL,
            mimeType = MIME_TYPE
        )

        var fileReadyCallback: ((FileAttachment) -> Unit)? = null

        whenever(
            fileRepository.uploadFile(
                conversationId = eq(CONVERSATION_ID),
                fileUri = eq(fileUri),
                fileId = any(),
                senderId = eq(USER_1_ID),
                onFileReady = any(),
                onProgress = any()
            )
        ).thenAnswer { invocation ->
            val onFileReady =
                invocation.getArgument<(FileAttachment) -> Unit>(4)

            val onProgress =
                invocation.getArgument<(String, Float) -> Unit>(5)

            fileReadyCallback = onFileReady

            onFileReady(attachment)
            onProgress(FILE_ID, 0.5f)

            assertEquals(
                attachment,
                viewModel.pendingFileMessage.value
                    ?.fileAttachments
                    ?.first()
            )

            assertEquals(
                FILE_ID,
                viewModel.uploadingFileId.value
            )

            assertEquals(
                0.5f,
                viewModel.fileUploadProgress.value
            )

            attachment
        }

        viewModel.sendFile(
            conversationId = CONVERSATION_ID,
            fileUri = fileUri,
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()

        val messageCaptor = argumentCaptor<Message>()

        verify(conversationRepository).sendMessage(messageCaptor.capture())

        val message = messageCaptor.firstValue

        assertEquals(MessageType.FILE, message.messageType)
        assertEquals(USER_1_ID, message.senderId)
        assertEquals(
            listOf(attachment),
            message.fileAttachments
        )

        verify(fileRepository).refreshMediaItems(CONVERSATION_ID)

        assertNull(viewModel.uploadingFileId.value)
        assertNull(viewModel.fileUploadProgress.value)
        assertNull(viewModel.pendingFileMessage.value)

        fileReadyCallback?.invoke(attachment)
        assertNull(viewModel.pendingFileMessage.value)
    }

    @Test
    fun `should handle exception when sending file`() = runTest {
        stubFirebaseUser()

        val fileUri = mock<Uri>()

        whenever(fileUri.lastPathSegment)
            .thenReturn(null)

        whenever(
            fileRepository.uploadFile(
                conversationId = eq(CONVERSATION_ID),
                fileUri = eq(fileUri),
                fileId = any(),
                senderId = eq(USER_1_ID),
                onFileReady = any(),
                onProgress = any()
            )
        ).thenThrow(RuntimeException())

        viewModel.sendFile(
            conversationId = CONVERSATION_ID,
            fileUri = fileUri,
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()
        verify(conversationRepository, never()).sendMessage(any())

        assertNull(viewModel.uploadingFileId.value)
        assertNull(viewModel.fileUploadProgress.value)
        assertNull(viewModel.pendingFileMessage.value)
    }

    @Test
    fun `should open file`() = runTest {
        val uri = mock<Uri>()

        whenever(
            fileRepository.downloadFile(
                FILE_URL,
                FILE_NAME
            )
        ).thenReturn(uri)

        var returnedUri: Uri? = null

        viewModel.openFile(
            messageId = MESSAGE_ID,
            fileUrl = FILE_URL,
            fileName = FILE_NAME,
            onFileReady = {
                returnedUri = it
            }
        )

        advanceUntilIdle()

        assertSame(uri, returnedUri)
        assertNull(viewModel.openingFileMessageId.value)

        verify(fileRepository)
            .downloadFile(
                FILE_URL,
                FILE_NAME
            )
    }

    @Test
    fun `should handle exception when opening file`() = runTest {
        whenever(
            fileRepository.downloadFile(
                FILE_URL,
                FILE_NAME
            )
        ).thenThrow(RuntimeException())

        var callbackCalled = false

        viewModel.openFile(
            MESSAGE_ID,
            FILE_URL,
            FILE_NAME
        ) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
        assertNull(viewModel.openingFileMessageId.value)
    }

    @Test
    fun `should download file`() = runTest {
        viewModel.downloadFile(
            messageId = MESSAGE_ID,
            fileUrl = FILE_URL,
            fileName = FILE_NAME,
            mimeType = MIME_TYPE
        )

        advanceUntilIdle()

        assertEquals(
            MESSAGE_ID,
            viewModel.fileDownloadUiState.value.messageId
        )

        assertEquals(
            FileDownloadState.SUCCESS,
            viewModel.fileDownloadUiState.value.state
        )

        verify(fileRepository)
            .saveFileToDownloads(
                FILE_URL,
                FILE_NAME,
                MIME_TYPE
            )
    }

    @Test
    fun `should set failed state when downloading file fails`() = runTest {
        whenever(
            fileRepository.saveFileToDownloads(
                FILE_URL,
                FILE_NAME,
                MIME_TYPE
            )
        ).thenThrow(RuntimeException())

        viewModel.downloadFile(
            messageId = MESSAGE_ID,
            fileUrl = FILE_URL,
            fileName = FILE_NAME,
            mimeType = MIME_TYPE
        )

        advanceUntilIdle()

        assertEquals(
            MESSAGE_ID,
            viewModel.fileDownloadUiState.value.messageId
        )

        assertEquals(
            FileDownloadState.FAILED,
            viewModel.fileDownloadUiState.value.state
        )
    }

    @Test
    fun `should not send images when list is empty`() = runTest {
        viewModel.sendImages(
            conversationId = CONVERSATION_ID,
            imageUris = emptyList(),
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()

        verifyNoInteractions(fileRepository)
        verifyNoInteractions(conversationRepository)
    }

    @Test
    fun `should not send more than ten images`() = runTest {
        val uris = List(11) {
            mock<Uri>()
        }

        viewModel.sendImages(
            conversationId = CONVERSATION_ID,
            imageUris = uris,
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()

        verifyNoInteractions(fileRepository)
        verifyNoInteractions(conversationRepository)
    }

    @Test
    fun `should send images`() = runTest {
        val uri1 = mock<Uri>()
        val uri2 = mock<Uri>()

        val attachment = FileAttachment(
            id = FILE_ID,
            url = FILE_URL,
            mimeType = "image/jpeg"
        )

        whenever(
            fileRepository.uploadFile(
                conversationId = eq(CONVERSATION_ID),
                fileUri = any(),
                fileId = any(),
                senderId = eq(USER_1_ID),
                onFileReady = any(),
                onProgress = any()
            )
        ).thenAnswer { invocation ->
            val onFileReady =
                invocation.getArgument<(FileAttachment) -> Unit>(4)

            val onProgress =
                invocation.getArgument<(String, Float) -> Unit>(5)

            onFileReady(attachment)
            onProgress(FILE_ID, 0.5f)

            attachment
        }

        viewModel.sendImages(
            conversationId = CONVERSATION_ID,
            imageUris = listOf(uri1, uri2),
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()

        val captor = argumentCaptor<Message>()

        verify(conversationRepository).sendMessage(captor.capture())

        val message = captor.firstValue

        assertEquals(MessageType.IMAGE, message.messageType)
        assertEquals(USER_1_ID, message.senderId)
        assertEquals(
            listOf(
                attachment,
                attachment
            ),
            message.fileAttachments
        )

        verify(
            fileRepository,
            times(2)
        ).uploadFile(
            conversationId = eq(CONVERSATION_ID),
            fileUri = any(),
            fileId = any(),
            senderId = eq(USER_1_ID),
            onFileReady = any(),
            onProgress = any()
        )

        verify(fileRepository).refreshMediaItems(CONVERSATION_ID)

        assertNull(viewModel.pendingImageMessage.value)
    }

    @Test
    fun `should handle exception when sending images`() = runTest {
        val uri = mock<Uri>()

        whenever(
            fileRepository.uploadFile(
                conversationId = eq(CONVERSATION_ID),
                fileUri = eq(uri),
                fileId = any(),
                senderId = eq(USER_1_ID),
                onFileReady = any(),
                onProgress = any()
            )
        ).thenThrow(RuntimeException())

        viewModel.sendImages(
            conversationId = CONVERSATION_ID,
            imageUris = listOf(uri),
            currentUserId = USER_1_ID
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .sendMessage(any())

        assertNull(viewModel.pendingImageMessage.value)
    }

    @Test
    fun `should load current user`() = runTest {
        val user = setupUser(USER_1_ID)

        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(user))

        viewModel.loadCurrentUser(USER_1_ID)

        advanceUntilIdle()

        assertEquals(
            user,
            viewModel.currentUser.value
        )
    }

    @Test
    fun `should handle exception when loading current user`() = runTest {
        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        viewModel.loadCurrentUser(USER_1_ID)

        advanceUntilIdle()

        assertEquals(
            "",
            viewModel.currentUser.value.uid
        )
    }

    @Test
    fun `should preload media items`() = runTest {
        viewModel.preLoadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()

        verify(fileRepository).refreshMediaItems(CONVERSATION_ID)
    }

    @Test
    fun `should handle exception when preloading media items`() = runTest {
        whenever(
            fileRepository.refreshMediaItems(CONVERSATION_ID)
        ).thenThrow(RuntimeException())

        viewModel.preLoadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()

        verify(fileRepository).refreshMediaItems(CONVERSATION_ID)
    }

    @Test
    fun `should update typing text`() = runTest {
        stubFirebaseUser()

        viewModel.updateTypingText(
            conversationId = CONVERSATION_ID,
            text = REQUEST_TEXT
        )

        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingText(
                conversationId = CONVERSATION_ID,
                userId = USER_1_ID,
                text = REQUEST_TEXT
            )
    }

    @Test
    fun `should cancel previous typing text update`() = runTest {
        stubFirebaseUser()

        viewModel.updateTypingText(
            CONVERSATION_ID,
            "First"
        )

        runCurrent()

        viewModel.updateTypingText(
            CONVERSATION_ID,
            "Second"
        )

        advanceUntilIdle()

        verify(conversationRepository, never())
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                "First"
            )

        verify(conversationRepository)
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                "Second"
            )
    }

    @Test
    fun `should handle exception when updating typing text`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                REQUEST_TEXT
            )
        ).thenThrow(RuntimeException())

        viewModel.updateTypingText(
            CONVERSATION_ID,
            REQUEST_TEXT
        )

        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                REQUEST_TEXT
            )
    }

    @Test
    fun `should clear typing text`() = runTest {
        stubFirebaseUser()

        viewModel.clearTypingText(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingText(
                conversationId = CONVERSATION_ID,
                userId = USER_1_ID,
                text = ""
            )
    }

    @Test
    fun `should cancel pending typing update when clearing text`() = runTest {
        stubFirebaseUser()

        viewModel.updateTypingText(
            CONVERSATION_ID,
            "Typing"
        )

        runCurrent()

        viewModel.clearTypingText(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository, never())
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                "Typing"
            )

        verify(conversationRepository)
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                ""
            )
    }

    @Test
    fun `should handle exception when clearing typing text`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                ""
            )
        ).thenThrow(RuntimeException())

        viewModel.clearTypingText(CONVERSATION_ID)

        advanceUntilIdle()

        verify(conversationRepository)
            .updateTypingText(
                CONVERSATION_ID,
                USER_1_ID,
                ""
            )
    }

}
