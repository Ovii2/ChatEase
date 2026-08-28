package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatInfoUiState
import com.example.chatease.util.MainDispatcherRule
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GroupChatInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val groupRepository: GroupRepository = mock()
    private val userRepository: UserRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val imageUtils: ImageUtils = mock()
    private val fileRepository: FileRepository = mock()
    private lateinit var viewModel: GroupChatInfoViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val CONVERSATION_ID = "conversation_1"
        private const val IMAGE_URL = "https://test.com/group.jpg"
        private const val GROUP_NAME = "Test Group"
    }

    @Before
    fun setUp() {
        viewModel = GroupChatInfoViewModel(
            auth = auth,
            groupRepository = groupRepository,
            userRepository = userRepository,
            conversationRepository = conversationRepository,
            imageUtils = imageUtils,
            fileRepository = fileRepository
        )
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun setupGroup(
        ownerId: String = USER_1_ID,
        userIds: List<String> = listOf(USER_1_ID, USER_2_ID)
    ): Group {
        return Group(
            conversationId = CONVERSATION_ID,
            ownerId = ownerId,
            userIds = userIds,
            visibleToUserIds = emptyList(),
            name = GROUP_NAME,
            imageUrl = null
        )
    }

    private fun setupUser(userId: String): User {
        return User(uid = userId)
    }

    private fun TestScope.waitForImageUpdate() {
        runCurrent()

        repeat(200) {
            if (!viewModel.isUpdating.value) {
                runCurrent()
                return
            }

            Thread.sleep(5)
            runCurrent()
        }

        throw AssertionError("Image update did not finish")
    }

    @Test
    fun `should return current user id`() {
        stubFirebaseUser()
        assertEquals(USER_1_ID, viewModel.currentUserId)
    }

    @Test
    fun `should return empty current user id when current user is null`() {
        whenever(auth.currentUser).thenReturn(null)
        assertEquals("", viewModel.currentUserId)
    }

    @Test
    fun `should load group with members`() = runTest {
        val group = setupGroup()
        val user1 = setupUser(USER_1_ID)
        val user2 = setupUser(USER_2_ID)

        whenever(groupRepository.observeGroup(CONVERSATION_ID)).thenReturn(flowOf(group))
        whenever(userRepository.observeUser(USER_1_ID)).thenReturn(flowOf(user1))
        whenever(userRepository.observeUser(USER_2_ID)).thenReturn(flowOf(user2))
        whenever(fileRepository.observeMediaItems(CONVERSATION_ID)).thenReturn(flowOf(emptyList()))

        viewModel.loadGroup(CONVERSATION_ID)
        advanceUntilIdle()

        val state = viewModel.uiState.value as GroupChatInfoUiState.Success

        assertSame(group, state.group)
        assertEquals(listOf(user1, user2), state.members)
        assertTrue(state.media.isEmpty())
    }

    @Test
    fun `should load group without members`() = runTest {
        val group = setupGroup(userIds = emptyList())

        whenever(groupRepository.observeGroup(CONVERSATION_ID)).thenReturn(flowOf(group))
        whenever(fileRepository.observeMediaItems(CONVERSATION_ID)).thenReturn(flowOf(emptyList()))

        viewModel.loadGroup(CONVERSATION_ID)
        advanceUntilIdle()

        val state = viewModel.uiState.value as GroupChatInfoUiState.Success

        assertSame(group, state.group)
        assertTrue(state.members.isEmpty())
        assertTrue(state.media.isEmpty())
    }

    @Test
    fun `should set error when loading group fails`() = runTest {
        whenever(groupRepository.observeGroup(CONVERSATION_ID)).thenReturn(
            flow {
                throw RuntimeException("Failed")
            }
        )

        viewModel.loadGroup(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(GroupChatInfoUiState.Error("Failed"), viewModel.uiState.value)
    }

    @Test
    fun `should use default error when loading group fails without message`() = runTest {
        whenever(groupRepository.observeGroup(CONVERSATION_ID)).thenReturn(
            flow {
                throw RuntimeException()
            }
        )

        viewModel.loadGroup(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(
            GroupChatInfoUiState.Error("Failed to load group"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should cancel previous group loading job`() = runTest {
        val group = setupGroup(userIds = emptyList())

        whenever(groupRepository.observeGroup(CONVERSATION_ID)).thenReturn(
            flow {
                awaitCancellation()
            },
            flowOf(group)
        )
        whenever(fileRepository.observeMediaItems(CONVERSATION_ID)).thenReturn(flowOf(emptyList()))

        viewModel.loadGroup(CONVERSATION_ID)
        runCurrent()
        viewModel.loadGroup(CONVERSATION_ID)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GroupChatInfoUiState.Success)
    }

    @Test
    fun `should leave group`() = runTest {
        stubFirebaseUser()

        viewModel.leaveGroup(CONVERSATION_ID)
        advanceUntilIdle()

        verify(groupRepository).leaveGroup(
            conversationId = CONVERSATION_ID,
            currentUserId = USER_1_ID
        )
    }

    @Test
    fun `should handle exception when leaving group`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroup(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.leaveGroup(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(GroupChatInfoUiState.Error("Failed"), viewModel.uiState.value)
    }

    @Test
    fun `should use default error when leaving group fails without message`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroup(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.leaveGroup(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(
            GroupChatInfoUiState.Error("Failed to leave the group"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should leave group as owner and delete conversation`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroupAsOwner(CONVERSATION_ID, USER_1_ID))
            .thenReturn(true)

        val completion = async {
            viewModel.ownerLeaveCompleted.first()
        }

        runCurrent()
        viewModel.leaveGroupAsOwner(CONVERSATION_ID)
        advanceUntilIdle()
        completion.await()

        verify(groupRepository).leaveGroupAsOwner(CONVERSATION_ID, USER_1_ID)
        verify(conversationRepository).deleteConversationWithMessages(CONVERSATION_ID)
    }

    @Test
    fun `should leave group as owner without deleting conversation`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroupAsOwner(CONVERSATION_ID, USER_1_ID))
            .thenReturn(false)

        val completion = async {
            viewModel.ownerLeaveCompleted.first()
        }

        runCurrent()
        viewModel.leaveGroupAsOwner(CONVERSATION_ID)
        advanceUntilIdle()
        completion.await()

        verify(conversationRepository, never()).deleteConversationWithMessages(any())
    }

    @Test
    fun `should handle exception when owner leaves group`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroupAsOwner(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException("Failed"))

        viewModel.leaveGroupAsOwner(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(GroupChatInfoUiState.Error("Failed"), viewModel.uiState.value)
        verify(conversationRepository, never()).deleteConversationWithMessages(any())
    }

    @Test
    fun `should use default error when owner leaving fails without message`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.leaveGroupAsOwner(CONVERSATION_ID, USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.leaveGroupAsOwner(CONVERSATION_ID)
        advanceUntilIdle()

        assertEquals(
            GroupChatInfoUiState.Error("Failed to leave the group as owner"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should set owner to true`() = runTest {
        stubFirebaseUser()

        val group = setupGroup(ownerId = USER_1_ID)

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(group)

        viewModel.checkIfUserIsGroupOwner(CONVERSATION_ID)
        advanceUntilIdle()

        assertTrue(viewModel.isOwner.value)
    }

    @Test
    fun `should set owner to false when current user is not owner`() = runTest {
        stubFirebaseUser()

        val group = setupGroup(ownerId = USER_2_ID)

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(group)

        viewModel.checkIfUserIsGroupOwner(CONVERSATION_ID)
        advanceUntilIdle()

        assertFalse(viewModel.isOwner.value)
    }

    @Test
    fun `should set owner to false when owner check fails`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.checkIfUserIsGroupOwner(CONVERSATION_ID)
        advanceUntilIdle()

        assertFalse(viewModel.isOwner.value)
    }

    @Test
    fun `should set group member to true`() = runTest {
        stubFirebaseUser()

        val group = setupGroup(userIds = listOf(USER_1_ID, USER_2_ID))

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(group)

        viewModel.checkIfUserIsGroupMember(CONVERSATION_ID)
        advanceUntilIdle()

        assertTrue(viewModel.isGroupMember.value)
    }

    @Test
    fun `should set group member to false`() = runTest {
        stubFirebaseUser()

        val group = setupGroup(userIds = listOf(USER_2_ID))

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenReturn(group)

        viewModel.checkIfUserIsGroupMember(CONVERSATION_ID)
        advanceUntilIdle()

        assertFalse(viewModel.isGroupMember.value)
    }

    @Test
    fun `should set group member to false when member check fails`() = runTest {
        stubFirebaseUser()

        whenever(groupRepository.getGroupByConversationId(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.checkIfUserIsGroupMember(CONVERSATION_ID)
        advanceUntilIdle()

        assertFalse(viewModel.isGroupMember.value)
    }

    @Test
    fun `should update group profile image`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri)).thenReturn(compressedUri)
        whenever(imageUtils.isFileSizeValid(compressedUri)).thenReturn(true)
        whenever(
            groupRepository.uploadGroupProfileImage(
                conversationId = CONVERSATION_ID,
                imageUri = compressedUri
            )
        ).thenReturn(IMAGE_URL)

        viewModel.updateGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = imageUri
        )

        waitForImageUpdate()

        verify(imageUtils).compressImage(imageUri)
        verify(imageUtils).isFileSizeValid(compressedUri)
        verify(groupRepository).uploadGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = compressedUri
        )
        verify(groupRepository).updateGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUrl = IMAGE_URL
        )

        assertFalse(viewModel.isUpdating.value)
    }

    @Test
    fun `should reject group image when file is too large`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri)).thenReturn(compressedUri)
        whenever(imageUtils.isFileSizeValid(compressedUri)).thenReturn(false)

        viewModel.updateGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = imageUri
        )

        waitForImageUpdate()

        assertEquals(
            GroupChatInfoUiState.Error("Image is too large"),
            viewModel.uiState.value
        )
        assertFalse(viewModel.isUpdating.value)
        verify(groupRepository, never()).uploadGroupProfileImage(any(), any())
        verify(groupRepository, never()).updateGroupProfileImage(any(), any())
    }

    @Test
    fun `should handle exception when updating group image`() = runTest {
        val imageUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenThrow(RuntimeException("Failed"))

        viewModel.updateGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = imageUri
        )

        waitForImageUpdate()

        assertEquals(GroupChatInfoUiState.Error("Failed"), viewModel.uiState.value)
        assertFalse(viewModel.isUpdating.value)
    }

    @Test
    fun `should use default error when updating group image fails without message`() = runTest {
        val imageUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri)).thenThrow(RuntimeException())

        viewModel.updateGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = imageUri
        )

        waitForImageUpdate()

        assertEquals(
            GroupChatInfoUiState.Error("Failed to update group image"),
            viewModel.uiState.value
        )
        assertFalse(viewModel.isUpdating.value)
    }

    @Test
    fun `should update group name`() = runTest {
        viewModel.updateGroupName(
            conversationId = CONVERSATION_ID,
            groupName = GROUP_NAME
        )

        advanceUntilIdle()

        verify(groupRepository).updateGroupName(CONVERSATION_ID, GROUP_NAME)
    }

    @Test
    fun `should handle exception when updating group name`() = runTest {
        whenever(groupRepository.updateGroupName(CONVERSATION_ID, GROUP_NAME))
            .thenThrow(RuntimeException("Failed"))

        viewModel.updateGroupName(CONVERSATION_ID, GROUP_NAME)
        advanceUntilIdle()

        assertEquals(GroupChatInfoUiState.Error("Failed"), viewModel.uiState.value)
    }

    @Test
    fun `should use default error when updating group name fails without message`() = runTest {
        whenever(groupRepository.updateGroupName(CONVERSATION_ID, GROUP_NAME))
            .thenThrow(RuntimeException())

        viewModel.updateGroupName(CONVERSATION_ID, GROUP_NAME)
        advanceUntilIdle()

        assertEquals(
            GroupChatInfoUiState.Error("Failed to update group name"),
            viewModel.uiState.value
        )
    }

}
