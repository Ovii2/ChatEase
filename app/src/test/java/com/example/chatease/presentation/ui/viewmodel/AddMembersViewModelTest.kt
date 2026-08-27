package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.AddMembersUiState
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
class AddMembersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val groupRepository: GroupRepository = mock()
    private val userRepository: UserRepository = mock()
    private val contactsRepository: ContactsRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: AddMembersViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val USER_4_ID = "4"
        private const val CONVERSATION_ID = "1"
        private const val CATEGORY_ID = "1"
    }

    @Before
    fun setUp() {
        viewModel = AddMembersViewModel(
            auth = auth,
            userRepository = userRepository,
            groupRepository = groupRepository,
            contactsRepository = contactsRepository
        )
    }

    private fun setupGroup() = Group(
        conversationId = CONVERSATION_ID,
        userIds = listOf(USER_1_ID, USER_3_ID, USER_4_ID),
        adminIds = listOf(USER_1_ID),
        visibleToUserIds = emptyList(),
        ownerId = USER_1_ID,
        name = "Test Group",
        imageUrl = null,
        removedAtByUserId = emptyMap(),
        categoryId = CATEGORY_ID
    )

    private fun setupEmptyGroup() = setupGroup()
        .copy(
            userIds = emptyList()
        )

    private fun setupContact(userIds: List<String>) = Contact(
        id = USER_1_ID,
        userIds = userIds,
        createdAt = System.currentTimeMillis()
    )

    private fun setupUser(userId: String): User = User(
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

    private fun stubMembers() {
        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(setupUser(USER_1_ID)))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        whenever(userRepository.observeUser(USER_3_ID))
            .thenReturn(flowOf(setupUser(USER_3_ID)))

        whenever(userRepository.observeUser(USER_4_ID))
            .thenReturn(flowOf(setupUser(USER_4_ID)))
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
    fun `should load members`() = runTest {
        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(setupGroup())
        stubFirebaseUser()
        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(listOf(setupContact(userIds = listOf(USER_1_ID, USER_2_ID))))

        stubMembers()

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            AddMembersUiState.Success(
                members = listOf(
                    setupUser(USER_2_ID)
                ),
                selectedMemberIds = emptySet()
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should set success with empty members when there are no available contacts`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(setupGroup())

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(emptyList())

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            AddMembersUiState.Success(
                members = emptyList(),
                selectedMemberIds = emptySet()
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when loading members`() = runTest {
        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            AddMembersUiState.Error(
                message = "Failed to load members"
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should add member to selection`() = runTest {
        viewModel.searchMembers("")

        viewModel.toggleMemberSelection(USER_1_ID)

        assertEquals(
            AddMembersUiState.Success(
                members = emptyList(),
                selectedMemberIds = setOf(USER_1_ID)
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should remove member from selection`() = runTest {
        viewModel.searchMembers("")

        viewModel.toggleMemberSelection(USER_1_ID)
        viewModel.toggleMemberSelection(USER_1_ID)

        assertEquals(
            AddMembersUiState.Success(
                members = emptyList(),
                selectedMemberIds = emptySet()
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should return all members when search query is empty`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(setupGroup())

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(listOf(setupContact(userIds = listOf(USER_1_ID, USER_2_ID))))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            AddMembersUiState.Success(
                members = listOf(setupUser(USER_2_ID)),
                selectedMemberIds = emptySet()
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should return matching members when search query is not empty`() = runTest {
        val alice = setupUser(USER_2_ID).copy(fullName = "Alice Smith")
        val bob = setupUser(USER_4_ID).copy(fullName = "Bob Jones")

        stubFirebaseUser()

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(
                setupGroup().copy(
                    userIds = listOf(USER_1_ID, USER_3_ID)
                )
            )
        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(
                listOf(
                    setupContact(listOf(USER_1_ID, USER_2_ID)),
                    setupContact(listOf(USER_1_ID, USER_4_ID))
                )
            )
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(alice))
        whenever(userRepository.observeUser(USER_4_ID))
            .thenReturn(flowOf(bob))

        viewModel.loadMembers(CONVERSATION_ID)

        advanceUntilIdle()
        viewModel.searchMembers("Alice")
        assertEquals(
            AddMembersUiState.Success(
                members = listOf(alice),
                selectedMemberIds = emptySet()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should clear search`() = runTest {
        viewModel.clearSearch()

        assertEquals("", viewModel.searchValue.value)
    }

    @Test
    fun `should update search value`() = runTest {
        viewModel.onSearchValueChange("Test")

        assertEquals("Test", viewModel.searchValue.value)
    }

    @Test
    fun `should add members`() = runTest {
        viewModel.searchMembers("")

        viewModel.toggleMemberSelection(USER_1_ID)
        viewModel.toggleMemberSelection(USER_2_ID)

        viewModel.addMembers(CONVERSATION_ID)

        advanceUntilIdle()
        verify(groupRepository)
            .addMembers(
                conversationId = CONVERSATION_ID,
                memberIds = listOf(USER_1_ID, USER_2_ID)
            )
    }

    @Test
    fun `should handle exception when adding members`() = runTest {
        whenever(
            groupRepository.addMembers(
                conversationId = CONVERSATION_ID,
                memberIds = listOf(USER_1_ID, USER_2_ID)
            )
        ).thenThrow(RuntimeException())

        viewModel.searchMembers("")

        viewModel.toggleMemberSelection(USER_1_ID)
        viewModel.toggleMemberSelection(USER_2_ID)

        viewModel.addMembers(CONVERSATION_ID)

        advanceUntilIdle()
        verify(groupRepository).addMembers(
            conversationId = CONVERSATION_ID,
            memberIds = listOf(USER_1_ID, USER_2_ID)
        )
    }


}
