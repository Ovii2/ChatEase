package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class GroupChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val userRepository: UserRepository = mock()
    private val groupRepository: GroupRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: GroupChatViewModel

    companion object {

        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val MEDIA_ITEM_ID = "1"
        private const val MESSAGE_ID = "1"
        private const val CONVERSATION_ID = "1"
        private const val ERROR_MESSAGE = "Failed"
    }

    @Before
    fun setUp() {
        viewModel = GroupChatViewModel(
            auth = auth,
            userRepository = userRepository,
            groupRepository = groupRepository,
            conversationRepository = conversationRepository
        )
    }

    private fun setupGroup() = Group(
        conversationId = CONVERSATION_ID,
        userIds = listOf(USER_1_ID, USER_2_ID, USER_3_ID)
    )

    private fun setupUser(userId: String): User {
        return User(uid = userId)
    }

    private fun setupMediaItem(mediaType: MediaType) = MediaItem(
        id = MEDIA_ITEM_ID,
        thumbnailUrl = "",
        mediaUrl = "",
        type = mediaType
    )

    private fun setupMessage() = Message(
        messageId = MESSAGE_ID
    )

    private fun setupConversation() = Conversation(
        id = CONVERSATION_ID,
        participantIds = listOf(USER_1_ID, USER_2_ID, USER_3_ID)
    )

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun stubGroupMembers(empty: Boolean = false) {
        listOf(
            USER_1_ID,
            USER_2_ID,
            USER_3_ID
        ).forEach { userId ->
            whenever(userRepository.observeUser(userId))
                .thenReturn(
                    if (empty) emptyFlow() else flowOf(setupUser(userId))
                )
        }
    }


    @Test
    fun `should load group conversation`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.observeGroup(CONVERSATION_ID))
            .thenReturn(flowOf(setupGroup()))

        whenever(conversationRepository.observeMessages(CONVERSATION_ID))
            .thenReturn(flowOf(listOf(setupMessage())))

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(setupConversation())

        stubGroupMembers()

        viewModel.loadGroupConversation(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            GroupChatUiState.Success(
                group = setupGroup(),
                members = listOf(setupUser(USER_1_ID), setupUser(USER_2_ID), setupUser(USER_3_ID)),
                messages = listOf(setupMessage())
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when observe users is called`() = runTest {
        whenever(groupRepository.observeGroup(CONVERSATION_ID))
            .thenReturn(emptyFlow())

        whenever(conversationRepository.observeMessages(CONVERSATION_ID))
            .thenReturn(flowOf(emptyList()))

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenThrow(RuntimeException(ERROR_MESSAGE))

        viewModel.loadGroupConversation(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            GroupChatUiState.Error(ERROR_MESSAGE),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should filter messages for former member and update existing success state`() = runTest {
        stubFirebaseUser()

        val removedAt = 150L

        val group = Group(
            conversationId = CONVERSATION_ID,
            userIds = listOf(USER_2_ID, USER_3_ID),
            removedAtByUserId = mapOf(USER_1_ID to removedAt)
        )

        val oldMessage = Message(
            messageId = "1",
            timeStamp = 100L
        )

        val newMessage = Message(
            messageId = "2",
            timeStamp = 200L
        )

        val messagesFlow = MutableStateFlow(
            listOf(oldMessage, newMessage)
        )

        whenever(groupRepository.observeGroup(CONVERSATION_ID))
            .thenReturn(flowOf(group))

        whenever(conversationRepository.observeMessages(CONVERSATION_ID))
            .thenReturn(messagesFlow)

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(setupConversation())

        stubGroupMembers(empty = true)

        viewModel.loadGroupConversation(CONVERSATION_ID)

        runCurrent()

        messagesFlow.value = listOf(
            oldMessage,
            newMessage,
            Message(
                messageId = "3",
                timeStamp = 120L
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value as GroupChatUiState.Success

        assertEquals(
            listOf(
                oldMessage,
                Message(
                    messageId = "3",
                    timeStamp = 120L
                )
            ),
            state.messages
        )
    }

    @Test
    fun `should handle exception when observing group messages`() = runTest {
        whenever(groupRepository.observeGroup(CONVERSATION_ID))
            .thenReturn(
                flow {
                    throw RuntimeException(ERROR_MESSAGE)
                }
            )

        whenever(conversationRepository.observeMessages(CONVERSATION_ID))
            .thenReturn(emptyFlow())

        whenever(conversationRepository.getConversation(CONVERSATION_ID))
            .thenReturn(setupConversation())

        stubGroupMembers(empty = true)

        viewModel.loadGroupConversation(CONVERSATION_ID)

        advanceUntilIdle()

        assertEquals(
            GroupChatUiState.Error(ERROR_MESSAGE),
            viewModel.uiState.value
        )
    }

}
