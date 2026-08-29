package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import com.example.chatease.R
import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.MyProfileUiState
import com.example.chatease.util.MainDispatcherRule
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
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
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class MyProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val userRepository: UserRepository = mock()
    private val contactsRepository: ContactsRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private val groupRepository: GroupRepository = mock()
    private val imageUtils: ImageUtils = mock()
    private lateinit var viewModel: MyProfileViewModel

    companion object {
        private const val USER_ID = "1"
    }

    private fun createViewModel(): MyProfileViewModel {
        viewModel = MyProfileViewModel(
            auth = auth,
            userRepository = userRepository,
            contactsRepository = contactsRepository,
            conversationRepository = conversationRepository,
            groupRepository = groupRepository,
            imageUtils = imageUtils
        )
        return viewModel
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_ID)
    }

    private fun setupConversation(
        deletedFor: List<String> = emptyList()
    ): Conversation {
        val conversation = mock<Conversation>()
        whenever(conversation.deletedFor).thenReturn(deletedFor)
        return conversation
    }

    private suspend fun stubSuccessfulInit(
        user: User = mock(),
        contacts: List<Contact> = emptyList(),
        conversations: List<Conversation> = emptyList(),
        groups: List<Group> = emptyList()
    ) {
        stubFirebaseUser()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(flowOf(user))

        whenever(contactsRepository.getContacts(USER_ID))
            .thenReturn(contacts)

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenReturn(conversations)

        whenever(groupRepository.getGroups(USER_ID))
            .thenReturn(groups)
    }

    private suspend fun stubOtherLoads() {
        whenever(contactsRepository.getContacts(USER_ID))
            .thenReturn(emptyList())

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenReturn(emptyList())

        whenever(groupRepository.getGroups(USER_ID))
            .thenReturn(emptyList())
    }

    private fun TestScope.waitForImageUpdate() {
        runCurrent()

        repeat(200) {
            val state = viewModel.uiState.value

            if (state is MyProfileUiState.Success && !state.isUploadingImage) {
                runCurrent()
                return
            }

            Thread.sleep(5)
            runCurrent()
        }

        throw AssertionError("Profile image update did not finish")
    }

    private fun statValue(
        state: MyProfileUiState.Success,
        label: Int
    ): String {
        return state.stats.first { it.label == label }.value
    }

    @Test
    fun `should start with loading state`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        assertEquals(
            MyProfileUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should do nothing when current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            MyProfileUiState.Loading,
            viewModel.uiState.value
        )
        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.conversations.value.isEmpty())
        assertTrue(viewModel.groups.value.isEmpty())
        verifyNoInteractions(
            userRepository,
            contactsRepository,
            conversationRepository,
            groupRepository,
            imageUtils
        )
    }

    @Test
    fun `should load user contacts conversations groups and stats`() = runTest {
        val user = mock<User>()
        val contact1 = mock<Contact>()
        val contact2 = mock<Contact>()
        val visibleConversation = setupConversation()
        val deletedConversation = setupConversation(
            deletedFor = listOf(USER_ID)
        )
        val group1 = mock<Group>()
        val group2 = mock<Group>()

        stubSuccessfulInit(
            user = user,
            contacts = listOf(contact1, contact2),
            conversations = listOf(
                visibleConversation,
                deletedConversation
            ),
            groups = listOf(group1, group2)
        )

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value as MyProfileUiState.Success

        assertSame(user, state.user)
        assertEquals(
            listOf(contact1, contact2),
            viewModel.contacts.value
        )
        assertEquals(
            listOf(visibleConversation),
            viewModel.conversations.value
        )
        assertEquals(
            listOf(group1, group2),
            viewModel.groups.value
        )
        assertEquals("1", statValue(state, R.string.chats))
        assertEquals("2", statValue(state, R.string.groups))
        assertEquals("2", statValue(state, R.string.contacts))
        assertFalse(state.isUploadingImage)
    }

    @Test
    fun `should refresh current user when state is already success`() = runTest {
        val firstUser = mock<User>()
        val secondUser = mock<User>()

        stubFirebaseUser()
        stubOtherLoads()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(flowOf(firstUser))
            .thenReturn(flowOf(secondUser))

        createViewModel()

        advanceUntilIdle()

        viewModel.loadCurrentUser()

        advanceUntilIdle()

        val state = viewModel.uiState.value as MyProfileUiState.Success

        assertSame(secondUser, state.user)
        assertFalse(state.isUploadingImage)
    }

    @Test
    fun `should handle current user loading exception`() = runTest {
        stubFirebaseUser()
        stubOtherLoads()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(
                flow {
                    throw RuntimeException("Failed")
                }
            )

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            MyProfileUiState.Error("Failed"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should use default error when current user loading fails without message`() = runTest {
        stubFirebaseUser()
        stubOtherLoads()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(
                flow {
                    throw RuntimeException()
                }
            )

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            MyProfileUiState.Error("Failed to load current user"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should load data without refreshing stats when user state is not success`() = runTest {
        val contact = mock<Contact>()
        val conversation = setupConversation()
        val group = mock<Group>()

        stubFirebaseUser()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(emptyFlow())

        whenever(contactsRepository.getContacts(USER_ID))
            .thenReturn(listOf(contact))

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenReturn(listOf(conversation))

        whenever(groupRepository.getGroups(USER_ID))
            .thenReturn(listOf(group))

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            MyProfileUiState.Loading,
            viewModel.uiState.value
        )
        assertEquals(listOf(contact), viewModel.contacts.value)
        assertEquals(listOf(conversation), viewModel.conversations.value)
        assertEquals(listOf(group), viewModel.groups.value)
    }

    @Test
    fun `should handle contacts loading exception`() = runTest {
        stubSuccessfulInit()

        whenever(contactsRepository.getContacts(USER_ID))
            .thenThrow(RuntimeException("Failed"))

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should handle contacts loading exception without message`() = runTest {
        stubSuccessfulInit()

        whenever(contactsRepository.getContacts(USER_ID))
            .thenThrow(RuntimeException())

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should handle conversations loading exception`() = runTest {
        stubSuccessfulInit()

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenThrow(RuntimeException("Failed"))

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.conversations.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should handle conversations loading exception without message`() = runTest {
        stubSuccessfulInit()

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenThrow(RuntimeException())

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.conversations.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should handle groups loading exception`() = runTest {
        stubSuccessfulInit()

        whenever(groupRepository.getGroups(USER_ID))
            .thenThrow(RuntimeException("Failed"))

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.groups.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should handle groups loading exception without message`() = runTest {
        stubSuccessfulInit()

        whenever(groupRepository.getGroups(USER_ID))
            .thenThrow(RuntimeException())

        createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.groups.value.isEmpty())
        assertTrue(viewModel.uiState.value is MyProfileUiState.Success)
    }

    @Test
    fun `should not update profile image when current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        viewModel.updateProfileImage(mock())

        runCurrent()

        verifyNoInteractions(imageUtils)
    }

    @Test
    fun `should not update profile image when state is not success`() = runTest {
        stubFirebaseUser()

        whenever(userRepository.observeUser(USER_ID))
            .thenReturn(emptyFlow())

        whenever(contactsRepository.getContacts(USER_ID))
            .thenReturn(emptyList())

        whenever(conversationRepository.getUserConversations(USER_ID))
            .thenReturn(emptyList())

        whenever(groupRepository.getGroups(USER_ID))
            .thenReturn(emptyList())

        createViewModel()

        advanceUntilIdle()

        viewModel.updateProfileImage(mock())

        runCurrent()

        verifyNoInteractions(imageUtils)
    }

    @Test
    fun `should update profile image`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        stubSuccessfulInit()

        createViewModel()

        advanceUntilIdle()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(true)

        viewModel.updateProfileImage(imageUri)

        waitForImageUpdate()

        verify(imageUtils).compressImage(imageUri)
        verify(imageUtils).isFileSizeValid(compressedUri)
        verify(userRepository).uploadProfileImage(
            userId = USER_ID,
            imageUri = compressedUri
        )

        val state = viewModel.uiState.value as MyProfileUiState.Success

        assertFalse(state.isUploadingImage)
    }

    @Test
    fun `should reject profile image when file is too large`() = runTest {
        val imageUri = mock<Uri>()
        val compressedUri = mock<Uri>()

        stubSuccessfulInit()

        createViewModel()

        advanceUntilIdle()

        whenever(imageUtils.compressImage(imageUri))
            .thenReturn(compressedUri)

        whenever(imageUtils.isFileSizeValid(compressedUri))
            .thenReturn(false)

        viewModel.updateProfileImage(imageUri)

        waitForImageUpdate()

        verify(imageUtils).compressImage(imageUri)
        verify(imageUtils).isFileSizeValid(compressedUri)
        verify(userRepository, never()).uploadProfileImage(
            userId = USER_ID,
            imageUri = compressedUri
        )

        val state = viewModel.uiState.value as MyProfileUiState.Success

        assertFalse(state.isUploadingImage)
    }

    @Test
    fun `should handle profile image update exception`() = runTest {
        val imageUri = mock<Uri>()

        stubSuccessfulInit()

        createViewModel()

        advanceUntilIdle()

        whenever(imageUtils.compressImage(imageUri))
            .thenThrow(RuntimeException("Failed"))

        viewModel.updateProfileImage(imageUri)

        waitForImageUpdate()

        val state = viewModel.uiState.value as MyProfileUiState.Success

        assertFalse(state.isUploadingImage)
    }

    @Test
    fun `should leave error state unchanged when image update finishes after user loading fails`() =
        runTest {
            val firstUser = mock<User>()
            val imageUri = mock<Uri>()
            val compressedUri = mock<Uri>()
            val compressionStarted = CountDownLatch(1)
            val releaseCompression = CountDownLatch(1)
            val compressionFinished = CountDownLatch(1)
            val validationFinished = CountDownLatch(1)

            stubFirebaseUser()
            stubOtherLoads()

            whenever(userRepository.observeUser(USER_ID))
                .thenReturn(flowOf(firstUser))
                .thenReturn(
                    flow {
                        throw RuntimeException("Failed")
                    }
                )

            createViewModel()

            advanceUntilIdle()

            whenever(imageUtils.compressImage(imageUri))
                .thenAnswer {
                    compressionStarted.countDown()
                    releaseCompression.await(2, TimeUnit.SECONDS)
                    compressionFinished.countDown()
                    compressedUri
                }

            whenever(imageUtils.isFileSizeValid(compressedUri))
                .thenAnswer {
                    validationFinished.countDown()
                    true
                }

            viewModel.updateProfileImage(imageUri)

            runCurrent()

            assertTrue(
                compressionStarted.await(
                    2,
                    TimeUnit.SECONDS
                )
            )

            viewModel.loadCurrentUser()

            runCurrent()

            assertEquals(
                MyProfileUiState.Error("Failed"),
                viewModel.uiState.value
            )

            releaseCompression.countDown()

            assertTrue(
                compressionFinished.await(
                    2,
                    TimeUnit.SECONDS
                )
            )

            repeat(200) {
                runCurrent()

                if (validationFinished.await(5, TimeUnit.MILLISECONDS)) {
                    runCurrent()
                    return@repeat
                }
            }

            runCurrent()

            assertEquals(
                MyProfileUiState.Error("Failed"),
                viewModel.uiState.value
            )
        }
}
