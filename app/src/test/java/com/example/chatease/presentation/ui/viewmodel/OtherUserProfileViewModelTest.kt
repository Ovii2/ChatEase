package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.repository.ConversationRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class OtherUserProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val userRepository: UserRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val groupRepository: GroupRepository = mock()

    private val viewModel = OtherUserProfileViewModel(
        auth = auth,
        userRepository = userRepository,
        conversationRepository = conversationRepository,
        groupRepository = groupRepository
    )

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val CONVERSATION_ID = "conversation_1"
    }

    private fun stubFirebaseUser(
        userId: String = USER_1_ID
    ) {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(userId)
    }

    private fun setupUser(
        userId: String
    ) = User(
        uid = userId
    )

    private fun setupGroup(
        userIds: List<String>
    ): Group {
        val group = mock<Group>()
        whenever(group.userIds).thenReturn(userIds)
        return group
    }

    @Test
    fun `should have initial state`() {
        assertEquals(User(), viewModel.user.value)
        assertFalse(viewModel.isUserConnected.value)
        assertFalse(viewModel.isUserBlocked.value)
        assertTrue(viewModel.mutualGroups.value.isEmpty())
    }

    @Test
    fun `should load user`() = runTest {
        val user = setupUser(USER_2_ID)

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(user))

        viewModel.loadUser(USER_2_ID)

        advanceUntilIdle()

        assertSame(
            user,
            viewModel.user.value
        )
    }

    @Test
    fun `should handle exception when loading user`() = runTest {
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flow {
                    throw RuntimeException("Failed")
                }
            )

        viewModel.loadUser(USER_2_ID)

        advanceUntilIdle()

        assertEquals(
            User(),
            viewModel.user.value
        )
    }

    @Test
    fun `should handle exception without message when loading user`() = runTest {
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        viewModel.loadUser(USER_2_ID)

        advanceUntilIdle()

        assertEquals(
            User(),
            viewModel.user.value
        )
    }

    @Test
    fun `should set connected state to true`() = runTest {
        whenever(userRepository.isUserConnected(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserConnected(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isUserConnected.value)
    }

    @Test
    fun `should set connected state to false`() = runTest {
        whenever(userRepository.isUserConnected(USER_2_ID))
            .thenReturn(false)

        viewModel.checkIfUserConnected(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserConnected.value)
    }

    @Test
    fun `should handle exception when checking connection`() = runTest {
        whenever(userRepository.isUserConnected(USER_2_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.checkIfUserConnected(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserConnected.value)
    }

    @Test
    fun `should handle exception without message when checking connection`() = runTest {
        whenever(userRepository.isUserConnected(USER_2_ID))
            .thenThrow(RuntimeException())

        viewModel.checkIfUserConnected(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserConnected.value)
    }

    @Test
    fun `should block user`() = runTest {
        viewModel.blockUser(USER_2_ID)

        advanceUntilIdle()

        verify(userRepository).blockUser(USER_2_ID)
        assertTrue(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should not change blocked state when blocking user fails`() = runTest {
        whenever(userRepository.blockUser(USER_2_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.blockUser(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should handle exception without message when blocking user`() = runTest {
        whenever(userRepository.blockUser(USER_2_ID))
            .thenThrow(RuntimeException())

        viewModel.blockUser(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should unblock user`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isUserBlocked.value)

        viewModel.unblockUser(USER_2_ID)

        advanceUntilIdle()

        verify(userRepository).unblockUser(USER_2_ID)
        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should keep blocked state when unblocking user fails`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        whenever(userRepository.unblockUser(USER_2_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.unblockUser(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should handle exception without message when unblocking user`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        whenever(userRepository.unblockUser(USER_2_ID))
            .thenThrow(RuntimeException())

        viewModel.unblockUser(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should set blocked state to true`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(true)

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        assertTrue(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should set blocked state to false`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenReturn(false)

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should handle exception when checking blocked user`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should handle exception without message when checking blocked user`() = runTest {
        whenever(userRepository.isUserBlocked(USER_2_ID))
            .thenThrow(RuntimeException())

        viewModel.checkIfUserIsBlocked(USER_2_ID)

        advanceUntilIdle()

        assertFalse(viewModel.isUserBlocked.value)
    }

    @Test
    fun `should use existing conversation`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenReturn(CONVERSATION_ID)

        var createdConversationId: String? = null

        viewModel.createNewConversation(USER_2_ID) {
            createdConversationId = it
        }

        advanceUntilIdle()

        assertEquals(
            CONVERSATION_ID,
            createdConversationId
        )

        verify(
            conversationRepository,
            never()
        ).createConversation(
            any(),
            any()
        )
    }

    @Test
    fun `should create conversation when existing conversation is not found`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenReturn(null)

        whenever(
            conversationRepository.createConversation(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                ),
                ConversationType.DIRECT
            )
        ).thenReturn(CONVERSATION_ID)

        var createdConversationId: String? = null

        viewModel.createNewConversation(USER_2_ID) {
            createdConversationId = it
        }

        advanceUntilIdle()

        assertEquals(
            CONVERSATION_ID,
            createdConversationId
        )

        verify(conversationRepository).createConversation(
            listOf(
                USER_1_ID,
                USER_2_ID
            ),
            ConversationType.DIRECT
        )
    }

    @Test
    fun `should sort participant ids`() = runTest {
        val currentUserId = "9"
        val selectedUserId = "2"

        stubFirebaseUser(currentUserId)

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    selectedUserId,
                    currentUserId
                )
            )
        ).thenReturn(CONVERSATION_ID)

        viewModel.createNewConversation(selectedUserId) {}

        advanceUntilIdle()

        verify(conversationRepository).getExistingConversationId(
            listOf(
                selectedUserId,
                currentUserId
            )
        )
    }

    @Test
    fun `should do nothing when creating conversation and current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
        verifyNoInteractions(conversationRepository)
    }

    @Test
    fun `should handle exception when checking existing conversation`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenThrow(RuntimeException("Failed"))

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)

        verify(
            conversationRepository,
            never()
        ).createConversation(
            any(),
            any()
        )
    }

    @Test
    fun `should handle exception without message when checking existing conversation`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenThrow(RuntimeException())

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
    }

    @Test
    fun `should handle exception when creating conversation`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenReturn(null)

        whenever(
            conversationRepository.createConversation(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                ),
                ConversationType.DIRECT
            )
        ).thenThrow(RuntimeException("Failed"))

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
    }

    @Test
    fun `should get mutual groups`() = runTest {
        stubFirebaseUser()

        val mutualGroup1 = setupGroup(
            listOf(
                USER_1_ID,
                USER_2_ID
            )
        )
        val currentUserOnlyGroup = setupGroup(
            listOf(
                USER_1_ID,
                USER_3_ID
            )
        )
        val otherUserOnlyGroup = setupGroup(
            listOf(
                USER_2_ID,
                USER_3_ID
            )
        )
        val mutualGroup2 = setupGroup(
            listOf(
                USER_3_ID,
                USER_2_ID,
                USER_1_ID
            )
        )

        whenever(groupRepository.getGroups(USER_1_ID))
            .thenReturn(
                listOf(
                    mutualGroup1,
                    currentUserOnlyGroup,
                    otherUserOnlyGroup,
                    mutualGroup2
                )
            )

        viewModel.getMutualGroups(USER_2_ID)

        advanceUntilIdle()

        assertEquals(
            listOf(
                mutualGroup1,
                mutualGroup2
            ),
            viewModel.mutualGroups.value
        )
    }

    @Test
    fun `should return empty mutual groups when no groups match`() = runTest {
        stubFirebaseUser()

        val group = setupGroup(
            listOf(
                USER_1_ID,
                USER_3_ID
            )
        )

        whenever(groupRepository.getGroups(USER_1_ID))
            .thenReturn(listOf(group))

        viewModel.getMutualGroups(USER_2_ID)

        advanceUntilIdle()

        assertTrue(
            viewModel.mutualGroups.value.isEmpty()
        )
    }

    @Test
    fun `should do nothing when getting mutual groups and current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.getMutualGroups(USER_2_ID)

        advanceUntilIdle()

        assertTrue(
            viewModel.mutualGroups.value.isEmpty()
        )
        verifyNoInteractions(groupRepository)
    }

    @Test
    fun `should handle exception when getting mutual groups`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroups(USER_1_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.getMutualGroups(USER_2_ID)

        advanceUntilIdle()

        assertTrue(
            viewModel.mutualGroups.value.isEmpty()
        )
    }

    @Test
    fun `should handle exception without message when getting mutual groups`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroups(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.getMutualGroups(USER_2_ID)

        advanceUntilIdle()

        assertTrue(
            viewModel.mutualGroups.value.isEmpty()
        )
    }

}
