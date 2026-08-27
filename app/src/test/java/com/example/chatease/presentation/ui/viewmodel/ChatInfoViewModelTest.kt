package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ChatInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val userRepository: UserRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val groupRepository: GroupRepository = mock()
    private val fileRepository: FileRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: ChatInfoViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val CONVERSATION_ID = "1"
        private const val MEDIA_ITEM_ID = "1"
    }

    @Before
    fun setUp() {
        viewModel = ChatInfoViewModel(
            auth = auth,
            userRepository = userRepository,
            conversationRepository = conversationRepository,
            groupRepository = groupRepository,
            fileRepository = fileRepository
        )
    }

    private fun setupDirectConversation(participantIds: List<String>) = Conversation(
        id = CONVERSATION_ID,
        type = ConversationType.DIRECT,
        creatorId = USER_1_ID,
        participantIds = participantIds,
        typingUserIds = emptyList(),
        typingTexts = emptyMap(),
        lastMessage = "Message",
        timestamp = System.currentTimeMillis(),
        unreadCounts = emptyMap(),
        deletedFor = emptyList(),
        lastMessageType = MessageType.TEXT,
        lastMessageSenderId = USER_1_ID
    )

    private fun setupMediaItem() = MediaItem(
        id = MEDIA_ITEM_ID,
        thumbnailUrl = "",
        mediaUrl = "",
        type = MediaType.FILE,
        senderId = USER_1_ID
    )

    private fun setupUser(userId: String) = User(
        uid = userId,
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList(),
        membership = Membership.FREE
    )

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    @Test
    fun `should return current user id`() {
        stubFirebaseUser()

        assertEquals(USER_1_ID, viewModel.currentUserId)
    }

    @Test
    fun `should return empty string when current user is null`() {
        whenever(auth.currentUser).thenReturn(null)

        assertEquals("", viewModel.currentUserId)
    }

    @Test
    fun `should load conversation`() = runTest {
        val otherUser = setupUser(USER_2_ID).copy(
            blockedUserIds = listOf(USER_1_ID)
        )
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(otherUser))
        stubFirebaseUser()
        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(setupDirectConversation(participantIds = listOf(USER_1_ID, USER_2_ID)))
        whenever(userRepository.isUserBlocked(USER_2_ID)).thenReturn(false)
        whenever(userRepository.isBlockedByUser(USER_1_ID)).thenReturn(false)

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).getConversation(CONVERSATION_ID)
        assertEquals(otherUser, viewModel.user.value)
        assertEquals(true, viewModel.isBlockedByOtherUser.value)
    }

    @Test
    fun `should handle exception when loading conversation`() = runTest {
        stubFirebaseUser()
        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.loadConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).getConversation(CONVERSATION_ID)
    }

    @Test
    fun `should block user`() = runTest {
        viewModel.blockUser(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).blockUser(USER_1_ID)
    }

    @Test
    fun `should handle exception when blocking users`() = runTest {
        whenever(userRepository.blockUser(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.blockUser(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).blockUser(USER_1_ID)
    }

    @Test
    fun `should unblock user`() = runTest {
        viewModel.unblockUser(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).unblockUser(USER_1_ID)
    }

    @Test
    fun `should handle exception when unblocking users`() = runTest {
        whenever(userRepository.unblockUser(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.unblockUser(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).unblockUser(USER_1_ID)
    }

    @Test
    fun `should delete conversation`() = runTest {
        viewModel.deleteConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).deleteConversation(CONVERSATION_ID)
    }

    @Test
    fun `should handle exception when deleting conversation`() = runTest {
        whenever(conversationRepository.deleteConversation(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.deleteConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).deleteConversation(CONVERSATION_ID)
    }

    @Test
    fun `should delete group conversation`() = runTest {
        viewModel.deleteGroupConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).deleteConversation(CONVERSATION_ID)
    }

    @Test
    fun `should handle exception when deleting group conversation`() = runTest {
        whenever(conversationRepository.deleteConversation(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.deleteGroupConversation(CONVERSATION_ID)

        advanceUntilIdle()
        verify(conversationRepository).deleteConversation(CONVERSATION_ID)
    }

    @Test
    fun `should load media items`() = runTest {
        whenever(fileRepository.getMediaItems(CONVERSATION_ID))
            .thenReturn(listOf(setupMediaItem()))
        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        viewModel.loadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()
        verify(fileRepository, times(2)).getMediaItems(CONVERSATION_ID)
    }

    @Test
    fun `should handle exception when loading media items`() = runTest {
        whenever(fileRepository.getMediaItems(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.loadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()
        verify(fileRepository).getMediaItems(CONVERSATION_ID)
    }

    @Test
    fun `should handle exception when observing user`() = runTest {
        stubFirebaseUser()

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(
                setupDirectConversation(
                    participantIds = listOf(USER_1_ID, USER_2_ID)
                )
            )
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(false)
        whenever(userRepository.isBlockedByUser(USER_2_ID))
            .thenReturn(false)
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        viewModel.loadConversation(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(
            User(),
            viewModel.user.value
        )
    }

}
