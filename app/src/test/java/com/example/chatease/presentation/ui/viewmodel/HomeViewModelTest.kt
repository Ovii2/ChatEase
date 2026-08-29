package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.data.local.datasource.CategoriesDataSource
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.HomeUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val userRepository: UserRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val groupRepository: GroupRepository = mock()
    private lateinit var viewModel: HomeViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val DIRECT_1_ID = "direct_1"
        private const val DIRECT_2_ID = "direct_2"
        private const val GROUP_ID = "group_1"
        private const val ALICE_NAME = "Alice Smith"
        private const val BOB_NAME = "Bob Jones"
        private const val ALICE_IMAGE = "alice.jpg"
        private const val GROUP_NAME = "Friends Group"
        private const val GROUP_IMAGE = "group.jpg"
    }

    private fun createViewModel(): HomeViewModel {
        viewModel = HomeViewModel(
            auth = auth,
            userRepository = userRepository,
            conversationRepository = conversationRepository,
            groupRepository = groupRepository
        )
        return viewModel
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun setupUser(
        userId: String,
        fullName: String = "",
        imageUrl: String? = null,
        blockedUserIds: List<String> = emptyList()
    ): User {
        return User(
            uid = userId,
            fullName = fullName,
            imageUrl = imageUrl,
            blockedUserIds = blockedUserIds
        )
    }

    private fun setupConversation(
        id: String,
        type: ConversationType,
        participantIds: List<String>,
        lastMessage: String = "",
        timestamp: Long = 0L,
        unreadCounts: Map<String, Int> = emptyMap(),
        lastMessageType: MessageType = MessageType.TEXT,
        lastMessageSenderId: String = ""
    ): Conversation {
        val conversation = mock<Conversation>()

        whenever(conversation.id).thenReturn(id)
        whenever(conversation.type).thenReturn(type)
        whenever(conversation.participantIds).thenReturn(participantIds)
        whenever(conversation.lastMessage).thenReturn(lastMessage)
        whenever(conversation.timestamp).thenReturn(timestamp)
        whenever(conversation.unreadCounts).thenReturn(unreadCounts)
        whenever(conversation.lastMessageType).thenReturn(lastMessageType)
        whenever(conversation.lastMessageSenderId).thenReturn(lastMessageSenderId)

        return conversation
    }

    private fun setupGroup(
        conversationId: String = GROUP_ID,
        userIds: List<String> = listOf(USER_1_ID, USER_2_ID),
        name: String = GROUP_NAME,
        imageUrl: String? = GROUP_IMAGE,
        categoryId: String = groupCategoryId()
    ): Group {
        val group = mock<Group>()

        whenever(group.conversationId).thenReturn(conversationId)
        whenever(group.userIds).thenReturn(userIds)
        whenever(group.name).thenReturn(name)
        whenever(group.imageUrl).thenReturn(imageUrl)
        whenever(group.categoryId).thenReturn(categoryId)

        return group
    }

    private fun groupCategoryId(): String {
        return CategoriesDataSource.categories.first { it.id != "all" }.id
    }

    private suspend fun stubHome(
        rawConversations: List<Conversation> = emptyList(),
        currentUser: User = setupUser(USER_1_ID),
        formerGroups: List<Group> = emptyList()
    ) {
        stubFirebaseUser()

        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(currentUser))

        whenever(conversationRepository.observeUserConversations(USER_1_ID))
            .thenReturn(flowOf(rawConversations))

        whenever(groupRepository.getGroupsVisibleToFormerMember(USER_1_ID))
            .thenReturn(formerGroups)
    }

    @Test
    fun `should return error when current user is not logged in`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Error("User not logged in"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should load home with empty conversations`() = runTest {
        val currentUser = setupUser(
            userId = USER_1_ID,
            fullName = "Current User"
        )

        stubHome(currentUser = currentUser)

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success

        assertEquals(currentUser, state.user)
        assertTrue(state.conversations.isEmpty())
        assertEquals(0, state.unreadMessages)
        assertEquals(
            listOf("all"),
            state.categories.map { it.id }
        )
    }

    @Test
    fun `should map direct conversation`() = runTest {
        val conversation = setupConversation(
            id = DIRECT_1_ID,
            type = ConversationType.DIRECT,
            participantIds = listOf(USER_1_ID, USER_2_ID),
            lastMessage = "Hello",
            timestamp = 100L,
            unreadCounts = mapOf(USER_1_ID to 2),
            lastMessageType = MessageType.TEXT,
            lastMessageSenderId = USER_2_ID
        )

        val otherUser = setupUser(
            userId = USER_2_ID,
            fullName = ALICE_NAME,
            imageUrl = ALICE_IMAGE,
            blockedUserIds = listOf(USER_1_ID)
        )

        stubHome(rawConversations = listOf(conversation))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(otherUser))

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        val item = state.conversations.single()

        assertEquals(DIRECT_1_ID, item.conversationId)
        assertEquals(ALICE_NAME, item.title)
        assertEquals(ALICE_IMAGE, item.imageUrl)
        assertEquals(listOf(otherUser), item.participants)
        assertEquals("Hello", item.lastMessage)
        assertEquals(100L, item.timestamp)
        assertEquals(2, item.unreadCount)
        assertFalse(item.isGroup)
        assertTrue(item.isBlockedByOtherUser)
        assertTrue(item.isCurrentUserGroupMember)
        assertEquals(null, item.categoryId)
        assertEquals(MessageType.TEXT, item.lastMessageType)
        assertEquals(USER_2_ID, item.lastMessageSenderId)
        assertEquals(2, state.unreadMessages)
    }

    @Test
    fun `should map direct conversation without other user`() = runTest {
        val conversation = setupConversation(
            id = DIRECT_1_ID,
            type = ConversationType.DIRECT,
            participantIds = listOf(USER_1_ID),
            lastMessage = "Hello",
            timestamp = 100L,
            unreadCounts = emptyMap()
        )

        stubHome(rawConversations = listOf(conversation))

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        val item = state.conversations.single()

        assertEquals("", item.title)
        assertEquals(null, item.imageUrl)
        assertTrue(item.participants.isEmpty())
        assertEquals(0, item.unreadCount)
        assertFalse(item.isBlockedByOtherUser)
        assertTrue(item.isCurrentUserGroupMember)
    }

    @Test
    fun `should map group conversation for current member`() = runTest {
        val categoryId = groupCategoryId()

        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID, USER_2_ID),
            lastMessage = "Group message",
            timestamp = 200L,
            unreadCounts = mapOf(USER_1_ID to 3),
            lastMessageType = MessageType.TEXT,
            lastMessageSenderId = USER_2_ID
        )

        val group = setupGroup(
            categoryId = categoryId
        )

        val user1 = setupUser(USER_1_ID, "Current User")
        val user2 = setupUser(USER_2_ID, "Group Member")

        stubHome(rawConversations = listOf(conversation))

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(flowOf(group))

        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(user1)

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        val item = state.conversations.single()

        assertEquals(GROUP_NAME, item.title)
        assertEquals(GROUP_IMAGE, item.imageUrl)
        assertEquals(listOf(user1, user2), item.participants)
        assertEquals("Group message", item.lastMessage)
        assertEquals(200L, item.timestamp)
        assertEquals(3, item.unreadCount)
        assertTrue(item.isGroup)
        assertFalse(item.isBlockedByOtherUser)
        assertTrue(item.isCurrentUserGroupMember)
        assertEquals(categoryId, item.categoryId)
        assertEquals(
            setOf("all", categoryId),
            state.categories.map { it.id }.toSet()
        )
        assertEquals(3, state.unreadMessages)
    }

    @Test
    fun `should hide group conversation details for former member`() = runTest {
        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID, USER_2_ID),
            lastMessage = "Hidden message",
            timestamp = 500L,
            unreadCounts = mapOf(USER_1_ID to 7)
        )

        val group = setupGroup(
            userIds = listOf(USER_2_ID)
        )

        val user2 = setupUser(USER_2_ID, "Group Member")

        stubHome(rawConversations = listOf(conversation))

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(flowOf(group))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        val item = state.conversations.single()

        assertEquals("", item.lastMessage)
        assertEquals(0L, item.timestamp)
        assertEquals(0, item.unreadCount)
        assertFalse(item.isCurrentUserGroupMember)
        assertEquals(0, state.unreadMessages)
    }

    @Test
    fun `should remove missing group from conversations`() = runTest {
        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID)
        )

        stubHome(rawConversations = listOf(conversation))

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(
                flow {
                    throw IllegalStateException("Group not found")
                }
            )

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success

        assertTrue(state.conversations.isEmpty())
    }

    @Test
    fun `should return error when observing group fails`() = runTest {
        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID)
        )

        stubHome(rawConversations = listOf(conversation))

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(
                flow {
                    throw RuntimeException("Failed")
                }
            )

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Error("Failed"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should return empty error message when loading fails without message`() = runTest {
        stubFirebaseUser()

        whenever(userRepository.observeUser(USER_1_ID))
            .thenReturn(flowOf(setupUser(USER_1_ID)))

        whenever(conversationRepository.observeUserConversations(USER_1_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Error(""),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should include former member conversation and remove duplicate`() = runTest {
        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID, USER_2_ID)
        )

        val group = setupGroup()

        stubHome(
            rawConversations = listOf(conversation),
            formerGroups = listOf(group)
        )

        whenever(conversationRepository.getConversation(GROUP_ID))
            .thenReturn(conversation)

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(flowOf(group))

        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(setupUser(USER_2_ID))

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success

        assertEquals(1, state.conversations.size)

        verify(conversationRepository)
            .getConversation(GROUP_ID)
    }

    @Test
    fun `should filter conversations by search ignoring case and spaces`() = runTest {
        val conversation1 = setupConversation(
            id = DIRECT_1_ID,
            type = ConversationType.DIRECT,
            participantIds = listOf(USER_1_ID, USER_2_ID)
        )

        val conversation2 = setupConversation(
            id = DIRECT_2_ID,
            type = ConversationType.DIRECT,
            participantIds = listOf(USER_1_ID, USER_3_ID)
        )

        stubHome(
            rawConversations = listOf(
                conversation1,
                conversation2
            )
        )

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flowOf(
                    setupUser(
                        userId = USER_2_ID,
                        fullName = ALICE_NAME
                    )
                )
            )

        whenever(userRepository.observeUser(USER_3_ID))
            .thenReturn(
                flowOf(
                    setupUser(
                        userId = USER_3_ID,
                        fullName = BOB_NAME
                    )
                )
            )

        createViewModel()

        advanceUntilIdle()

        viewModel.onSearchValueChange("  ALI  ")

        advanceTimeBy(301.milliseconds)
        runCurrent()

        var state = viewModel.uiState.value as HomeUiState.Success

        assertEquals(
            listOf(DIRECT_1_ID),
            state.conversations.map { it.conversationId }
        )

        viewModel.clearSearch()

        advanceTimeBy(301.milliseconds)
        runCurrent()

        state = viewModel.uiState.value as HomeUiState.Success

        assertEquals("", viewModel.searchValue.value)
        assertEquals(2, state.conversations.size)
    }

    @Test
    fun `should filter conversations by selected category`() = runTest {
        val categoryId = groupCategoryId()

        val directConversation = setupConversation(
            id = DIRECT_1_ID,
            type = ConversationType.DIRECT,
            participantIds = listOf(USER_1_ID, USER_2_ID)
        )

        val groupConversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID, USER_3_ID)
        )

        val group = setupGroup(
            userIds = listOf(USER_1_ID, USER_3_ID),
            categoryId = categoryId
        )

        stubHome(
            rawConversations = listOf(
                directConversation,
                groupConversation
            )
        )

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flowOf(
                    setupUser(
                        userId = USER_2_ID,
                        fullName = ALICE_NAME
                    )
                )
            )

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(flowOf(group))

        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(setupUser(USER_3_ID))

        createViewModel()

        advanceUntilIdle()

        viewModel.selectCategory(categoryId)

        runCurrent()

        var state = viewModel.uiState.value as HomeUiState.Success

        assertEquals(categoryId, viewModel.selectedCategory.value)
        assertEquals(
            listOf(GROUP_ID),
            state.conversations.map { it.conversationId }
        )

        viewModel.selectCategory("all")

        runCurrent()

        state = viewModel.uiState.value as HomeUiState.Success

        assertEquals(2, state.conversations.size)
    }

    @Test
    fun `should exclude group with different category`() = runTest {
        val categoryId = groupCategoryId()

        val conversation = setupConversation(
            id = GROUP_ID,
            type = ConversationType.GROUP,
            participantIds = listOf(USER_1_ID, USER_2_ID)
        )

        val group = setupGroup(
            categoryId = categoryId
        )

        stubHome(rawConversations = listOf(conversation))

        whenever(groupRepository.observeGroup(GROUP_ID))
            .thenReturn(flowOf(group))

        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(setupUser(USER_2_ID))

        createViewModel()

        advanceUntilIdle()

        viewModel.selectCategory("category_that_does_not_match")

        runCurrent()

        val state = viewModel.uiState.value as HomeUiState.Success

        assertTrue(state.conversations.isEmpty())
    }

    @Test
    fun `should update search value`() = runTest {
        stubHome()

        createViewModel()

        viewModel.onSearchValueChange("Alice")

        assertEquals(
            "Alice",
            viewModel.searchValue.value
        )
    }

    @Test
    fun `should update selected category`() = runTest {
        stubHome()

        createViewModel()

        viewModel.selectCategory("test_category")

        assertEquals(
            "test_category",
            viewModel.selectedCategory.value
        )
    }

}
