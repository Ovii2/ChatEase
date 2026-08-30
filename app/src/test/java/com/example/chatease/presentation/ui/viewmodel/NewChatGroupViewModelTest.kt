package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.NewChatGroupUiState
import com.example.chatease.util.MainDispatcherRule
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class NewChatGroupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val groupRepository: GroupRepository = mock()
    private val userRepository: UserRepository = mock()
    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val imageUtils: ImageUtils = mock()

    private val viewModel = NewChatGroupViewModel(
        groupRepository = groupRepository,
        userRepository = userRepository,
        auth = auth,
        conversationRepository = conversationRepository,
        imageUtils = imageUtils
    )

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val CONVERSATION_ID = "conversation_1"
        private const val GROUP_NAME = "Test Group"
        private const val IMAGE_URL = "https://test.com/group.jpg"
        private const val CATEGORY_ID = "work"
    }

    private fun setupUser(userId: String) = User(
        uid = userId
    )

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private suspend fun stubSuccessfulGroupCreation() {
        whenever(
            conversationRepository.createGroupConversation(
                any()
            )
        ).thenReturn(CONVERSATION_ID)
    }

    private fun TestScope.waitForImageUpdate() {
        advanceTimeBy(1000.milliseconds)
        runCurrent()

        repeat(200) {
            if (!viewModel.isUploadingImage.value) {
                runCurrent()
                return
            }

            Thread.sleep(5)
            runCurrent()
        }

        throw AssertionError("Group image update did not finish")
    }

    @Test
    fun `should have initial state`() {
        assertEquals("", viewModel.groupName.value)
        assertTrue(viewModel.members.value.isEmpty())
        assertTrue(viewModel.removedMemberIds.value.isEmpty())
        assertEquals("", viewModel.suggestedGroupName.value)
        assertNull(viewModel.groupImageUri.value)
        assertFalse(viewModel.isUploadingImage.value)
        assertEquals("friends", viewModel.selectedCategoryId.value)
        assertEquals(NewChatGroupUiState.Idle, viewModel.uiState.value)
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
    fun `should change group name`() {
        viewModel.onGroupNameChange(GROUP_NAME)

        assertEquals(GROUP_NAME, viewModel.groupName.value)
    }

    @Test
    fun `should set members`() {
        val members = listOf(
            setupUser(USER_2_ID),
            setupUser(USER_3_ID)
        )

        viewModel.setMembers(members)

        assertEquals(members, viewModel.members.value)
    }

    @Test
    fun `should remove member`() {
        val user2 = setupUser(USER_2_ID)
        val user3 = setupUser(USER_3_ID)

        viewModel.setMembers(
            listOf(
                user2,
                user3
            )
        )

        viewModel.removeMember(USER_2_ID)

        assertEquals(
            listOf(user3),
            viewModel.members.value
        )
        assertEquals(
            setOf(USER_2_ID),
            viewModel.removedMemberIds.value
        )
    }

    @Test
    fun `should keep removed member id when removing same member again`() {
        val user2 = setupUser(USER_2_ID)

        viewModel.setMembers(listOf(user2))

        viewModel.removeMember(USER_2_ID)
        viewModel.removeMember(USER_2_ID)

        assertTrue(viewModel.members.value.isEmpty())
        assertEquals(
            setOf(USER_2_ID),
            viewModel.removedMemberIds.value
        )
    }

    @Test
    fun `should observe members`() = runTest {
        val user2 = setupUser(USER_2_ID)
        val user3 = setupUser(USER_3_ID)

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(user2))

        whenever(userRepository.observeUser(USER_3_ID))
            .thenReturn(flowOf(user3))

        viewModel.observeMembers(
            listOf(
                USER_2_ID,
                USER_3_ID
            )
        )

        advanceUntilIdle()

        assertEquals(
            listOf(
                user2,
                user3
            ),
            viewModel.members.value
        )
    }

    @Test
    fun `should filter removed member when observing members`() = runTest {
        val user2 = setupUser(USER_2_ID)
        val user3 = setupUser(USER_3_ID)

        viewModel.removeMember(USER_2_ID)

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(user2))

        whenever(userRepository.observeUser(USER_3_ID))
            .thenReturn(flowOf(user3))

        viewModel.observeMembers(
            listOf(
                USER_2_ID,
                USER_3_ID
            )
        )

        advanceUntilIdle()

        assertEquals(
            listOf(user3),
            viewModel.members.value
        )
    }

    @Test
    fun `should handle exception when observing members`() = runTest {
        val existingMember = setupUser(USER_3_ID)

        viewModel.setMembers(listOf(existingMember))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flow {
                    throw RuntimeException("Failed")
                }
            )

        viewModel.observeMembers(
            listOf(USER_2_ID)
        )

        advanceUntilIdle()

        assertEquals(
            listOf(existingMember),
            viewModel.members.value
        )
    }

    @Test
    fun `should handle exception without message when observing members`() = runTest {
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        viewModel.observeMembers(
            listOf(USER_2_ID)
        )

        advanceUntilIdle()

        assertTrue(viewModel.members.value.isEmpty())
    }

    @Test
    fun `should suggest group name`() {
        viewModel.suggestGroupName()

        val suggestion = viewModel.suggestedGroupName.value

        assertTrue(suggestion.isNotBlank())
        assertEquals(2, suggestion.split(" ").size)
    }

    @Test
    fun `should refresh suggested group name`() {
        viewModel.refreshSuggestGroupName()

        assertTrue(viewModel.suggestedGroupName.value.isNotBlank())
    }

    @Test
    fun `should accept suggested group name`() {
        viewModel.acceptSuggestedGroupName(GROUP_NAME)

        assertEquals(
            GROUP_NAME,
            viewModel.groupName.value
        )
    }

    @Test
    fun `should select category`() {
        viewModel.selectCategory(CATEGORY_ID)

        assertEquals(
            CATEGORY_ID,
            viewModel.selectedCategoryId.value
        )
    }

    @Test
    fun `should reset ui state`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        whenever(
            conversationRepository.createGroupConversation(
                listOf("")
            )
        ).thenThrow(RuntimeException("Failed"))

        viewModel.createGroup {}

        advanceUntilIdle()

        assertEquals(
            NewChatGroupUiState.Error("Failed"),
            viewModel.uiState.value
        )

        viewModel.resetState()

        assertEquals(
            NewChatGroupUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should create group without image`() = runTest {
        stubFirebaseUser()
        stubSuccessfulGroupCreation()

        val user2 = setupUser(USER_2_ID)
        val user3 = setupUser(USER_3_ID)

        viewModel.setMembers(
            listOf(
                user2,
                user3
            )
        )
        viewModel.onGroupNameChange(GROUP_NAME)
        viewModel.selectCategory(CATEGORY_ID)

        var createdConversationId: String? = null

        viewModel.createGroup { conversationId ->
            createdConversationId = conversationId
        }

        advanceUntilIdle()

        verify(conversationRepository).createGroupConversation(
            listOf(
                USER_2_ID,
                USER_3_ID,
                USER_1_ID
            )
        )

        verify(groupRepository).createGroup(
            conversationId = CONVERSATION_ID,
            userIds = listOf(
                USER_2_ID,
                USER_3_ID,
                USER_1_ID
            ),
            adminIds = listOf(USER_1_ID),
            name = GROUP_NAME,
            ownerId = USER_1_ID,
            imageUrl = null,
            categoryId = CATEGORY_ID
        )

        verify(
            groupRepository,
            never()
        ).uploadGroupProfileImage(
            any(),
            any()
        )

        assertEquals(
            CONVERSATION_ID,
            createdConversationId
        )
        assertEquals(
            NewChatGroupUiState.Success,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should create distinct participant ids`() = runTest {
        stubFirebaseUser()
        stubSuccessfulGroupCreation()

        viewModel.setMembers(
            listOf(
                setupUser(USER_1_ID),
                setupUser(USER_2_ID),
                setupUser(USER_2_ID)
            )
        )

        viewModel.createGroup {}

        advanceUntilIdle()

        verify(conversationRepository).createGroupConversation(
            listOf(
                USER_1_ID,
                USER_2_ID
            )
        )

        verify(groupRepository).createGroup(
            conversationId = eq(CONVERSATION_ID),
            userIds = eq(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            ),
            adminIds = eq(listOf(USER_1_ID)),
            name = eq(""),
            ownerId = eq(USER_1_ID),
            imageUrl = eq(null),
            categoryId = eq("friends")
        )
    }

    @Test
    fun `should create group with image`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        stubFirebaseUser()
        stubSuccessfulGroupCreation()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(true)

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        whenever(
            groupRepository.uploadGroupProfileImage(
                conversationId = CONVERSATION_ID,
                imageUri = compressedUri
            )
        ).thenReturn(IMAGE_URL)

        viewModel.onGroupNameChange(GROUP_NAME)

        viewModel.createGroup {}

        advanceUntilIdle()

        verify(groupRepository).uploadGroupProfileImage(
            conversationId = CONVERSATION_ID,
            imageUri = compressedUri
        )

        verify(groupRepository).createGroup(
            conversationId = CONVERSATION_ID,
            userIds = listOf(USER_1_ID),
            adminIds = listOf(USER_1_ID),
            name = GROUP_NAME,
            ownerId = USER_1_ID,
            imageUrl = IMAGE_URL,
            categoryId = "friends"
        )

        assertEquals(
            NewChatGroupUiState.Success,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when creating conversation`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.createGroupConversation(
                listOf(USER_1_ID)
            )
        ).thenThrow(RuntimeException("Failed"))

        var callbackCalled = false

        viewModel.createGroup {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertEquals(
            NewChatGroupUiState.Error("Failed"),
            viewModel.uiState.value
        )
        assertFalse(callbackCalled)
    }

    @Test
    fun `should use default error when creating conversation fails without message`() = runTest {
        stubFirebaseUser()

        whenever(
            conversationRepository.createGroupConversation(
                listOf(USER_1_ID)
            )
        ).thenThrow(RuntimeException())

        viewModel.createGroup {}

        advanceUntilIdle()

        assertEquals(
            NewChatGroupUiState.Error("Failed to create group"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when uploading group image during creation`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        stubFirebaseUser()
        stubSuccessfulGroupCreation()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(true)

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        whenever(
            groupRepository.uploadGroupProfileImage(
                conversationId = CONVERSATION_ID,
                imageUri = compressedUri
            )
        ).thenThrow(RuntimeException("Upload failed"))

        viewModel.createGroup {}

        advanceUntilIdle()

        assertEquals(
            NewChatGroupUiState.Error("Upload failed"),
            viewModel.uiState.value
        )

        verify(
            groupRepository,
            never()
        ).createGroup(
            conversationId = any(),
            userIds = any(),
            adminIds = any(),
            name = any(),
            ownerId = any(),
            imageUrl = any(),
            categoryId = any()
        )
    }

    @Test
    fun `should handle exception when creating group`() = runTest {
        stubFirebaseUser()
        stubSuccessfulGroupCreation()

        whenever(
            groupRepository.createGroup(
                conversationId = CONVERSATION_ID,
                userIds = listOf(USER_1_ID),
                adminIds = listOf(USER_1_ID),
                name = "",
                ownerId = USER_1_ID,
                imageUrl = null,
                categoryId = "friends"
            )
        ).thenThrow(RuntimeException("Failed"))

        viewModel.createGroup {}

        advanceUntilIdle()

        assertEquals(
            NewChatGroupUiState.Error("Failed"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should set image upload state before delay`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(true)

        viewModel.updateGroupProfileImage(imageUri)

        runCurrent()

        assertTrue(viewModel.isUploadingImage.value)
        assertNull(viewModel.groupImageUri.value)

        waitForImageUpdate()

        assertFalse(viewModel.isUploadingImage.value)
    }

    @Test
    fun `should update group profile image`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(true)

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        assertSame(
            compressedUri,
            viewModel.groupImageUri.value
        )
        assertFalse(viewModel.isUploadingImage.value)

        verify(imageUtils).compressImage(imageUri)
        verify(imageUtils).isFileSizeValid(compressedUri)
    }

    @Test
    fun `should reject group profile image when file is too large`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(false)

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        assertNull(viewModel.groupImageUri.value)
        assertFalse(viewModel.isUploadingImage.value)

        verify(imageUtils).compressImage(imageUri)
        verify(imageUtils).isFileSizeValid(compressedUri)
    }

    @Test
    fun `should handle exception when preparing group profile image`() = runTest {
        val imageUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenThrow(RuntimeException("Failed"))

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        assertNull(viewModel.groupImageUri.value)
        assertFalse(viewModel.isUploadingImage.value)
    }

    @Test
    fun `should handle exception without message when preparing group profile image`() = runTest {
        val imageUri = mock<Uri>()

        whenever(imageUtils.compressImage(imageUri))
            .thenThrow(RuntimeException())

        viewModel.updateGroupProfileImage(imageUri)

        waitForImageUpdate()

        assertNull(viewModel.groupImageUri.value)
        assertFalse(viewModel.isUploadingImage.value)
    }

    @Test
    fun `should not use repositories when only changing local values`() {
        viewModel.onGroupNameChange(GROUP_NAME)
        viewModel.setMembers(listOf(setupUser(USER_2_ID)))
        viewModel.removeMember(USER_2_ID)
        viewModel.acceptSuggestedGroupName(GROUP_NAME)
        viewModel.selectCategory(CATEGORY_ID)
        viewModel.resetState()

        verifyNoInteractions(
            groupRepository,
            userRepository,
            conversationRepository,
            imageUtils
        )
    }

}
