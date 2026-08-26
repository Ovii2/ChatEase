package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatMembersUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GroupChatMembersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val groupRepository: GroupRepository = mock()
    private val userRepository: UserRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: GroupChatMembersViewModel

    companion object {
        private const val CONVERSATION_ID = "1"
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
    }

    @Before
    fun setUp() {
        viewModel = GroupChatMembersViewModel(
            auth = auth,
            groupRepository = groupRepository,
            userRepository = userRepository
        )
    }

    private fun setupGroup() = Group(
        conversationId = CONVERSATION_ID,
        userIds = listOf("1", "2", "3"),
        adminIds = listOf("1"),
        visibleToUserIds = emptyList(),
        ownerId = "1",
        name = "Test Group",
        imageUrl = null,
        removedAtByUserId = emptyMap(),
        categoryId = "1"
    )

    private fun setupEmptyGroup() = setupGroup().copy(userIds = emptyList())

    private fun setupUser(userId: String): User = User(
        uid = userId,
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList(),
        membership = Membership.FREE
    )

    private fun stubGroupMembers() {
        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(setupUser(USER_1_ID)))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        whenever(userRepository.observeUser(USER_3_ID))
            .thenReturn(flowOf(setupUser(USER_3_ID)))
    }

    @Test
    fun `should return current user id`() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)

        assertEquals(USER_1_ID, viewModel.currentUserId)
    }

    @Test
    fun `should return empty string when current user is null`() {
        whenever(auth.currentUser).thenReturn(null)

        assertEquals("", viewModel.currentUserId)
    }

    @Test
    fun `should load members`() = runTest {
        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(setupGroup())

        stubGroupMembers()

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            GroupChatMembersUiState.Success(
                members = listOf(
                    setupUser(USER_1_ID),
                    setupUser(USER_2_ID),
                    setupUser(USER_3_ID)
                ),
                adminIds = listOf(USER_1_ID),
                ownerId = USER_1_ID
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should set success with empty members when group has no members`() = runTest {
        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(setupEmptyGroup())

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            GroupChatMembersUiState.Success(
                members = emptyList(),
                adminIds = emptyList(),
                ownerId = ""
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when loading group members`() = runTest {
        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            GroupChatMembersUiState.Error("Failed to load group members"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should check if members is interface contacts`() = runTest {
        whenever(userRepository.isUserInContacts(USER_1_ID))
            .thenReturn(true)

        viewModel.checkIfMemberIsInContacts(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).isUserInContacts(USER_1_ID)
        assertEquals(true, viewModel.usersInContacts.value[USER_1_ID])
    }

    @Test
    fun `should handle exception when checking if user is in contacts`() = runTest {
        whenever(userRepository.isUserInContacts(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.checkIfMemberIsInContacts(USER_1_ID)

        advanceUntilIdle()
        verify(userRepository).isUserInContacts(USER_1_ID)
    }

    @Test
    fun `should promote user to admin`() = runTest {
        viewModel.addAdmin(CONVERSATION_ID, USER_2_ID)

        advanceUntilIdle()
        verify(groupRepository).promoteToAdmin(CONVERSATION_ID, USER_2_ID)
    }

    @Test
    fun `should handle exception when promoting user to admin`() = runTest {
        whenever(groupRepository.promoteToAdmin(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.addAdmin(CONVERSATION_ID, USER_1_ID)

        advanceUntilIdle()
        verify(groupRepository).promoteToAdmin(CONVERSATION_ID, USER_1_ID)
    }

    @Test
    fun `should demote user from admin`() = runTest {
        viewModel.removeAdmin(CONVERSATION_ID, USER_1_ID)

        advanceUntilIdle()
        verify(groupRepository).demoteFromAdmin(CONVERSATION_ID, USER_1_ID)
    }

    @Test
    fun `should handle exception when demoting user from admin`() = runTest {
        whenever(groupRepository.demoteFromAdmin(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.removeAdmin(CONVERSATION_ID, USER_1_ID)

        advanceUntilIdle()
        verify(groupRepository).demoteFromAdmin(CONVERSATION_ID, USER_1_ID)
    }

    @Test
    fun `should remove member`() = runTest {
        viewModel.removeMember(CONVERSATION_ID, USER_1_ID)

        advanceUntilIdle()
        verify(groupRepository).removeMember(CONVERSATION_ID, USER_1_ID)
    }

    @Test
    fun `should handle exception when removing member`() = runTest {
        whenever(groupRepository.removeMember(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.removeMember(CONVERSATION_ID, USER_1_ID)

        advanceUntilIdle()
        verify(groupRepository).removeMember(CONVERSATION_ID, USER_1_ID)
    }

}