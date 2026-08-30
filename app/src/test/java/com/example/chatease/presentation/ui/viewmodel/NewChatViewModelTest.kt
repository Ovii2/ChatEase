package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class NewChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val contactsRepository: ContactsRepository = mock()
    private val userRepository: UserRepository = mock()
    private val conversationRepository: ConversationRepository = mock()
    private lateinit var viewModel: NewChatViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val CONVERSATION_ID = "conversation_1"
    }

    private fun setupViewModel(): NewChatViewModel {
        viewModel = NewChatViewModel(
            auth = auth,
            contactsRepository = contactsRepository,
            userRepository = userRepository,
            conversationRepository = conversationRepository
        )
        return viewModel
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun setupUser(
        userId: String,
        fullName: String
    ) = User(
        uid = userId,
        fullName = fullName
    )

    private fun setupContact(
        userIds: List<String>
    ): Contact {
        val contact = mock<Contact>()
        whenever(contact.userIds).thenReturn(userIds)
        return contact
    }

    private suspend fun stubContacts(
        contacts: List<Contact> = emptyList()
    ) {
        stubFirebaseUser()
        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(contacts)
    }

    @Test
    fun `should load contacts and users on init`() = runTest {
        val contact1 = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )
        val contact2 = setupContact(
            listOf(USER_3_ID, USER_1_ID)
        )
        val user2 = setupUser(
            userId = USER_2_ID,
            fullName = "Alice Smith"
        )
        val user3 = setupUser(
            userId = USER_3_ID,
            fullName = "Bob Jones"
        )

        stubContacts(
            listOf(
                contact1,
                contact2
            )
        )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(user3)

        setupViewModel()

        advanceUntilIdle()

        assertEquals(
            listOf(
                contact1,
                contact2
            ),
            viewModel.contacts.value
        )
        assertEquals(
            listOf(
                user2,
                user3
            ),
            viewModel.users.value
        )
        assertEquals(
            listOf(
                user2,
                user3
            ),
            viewModel.filteredUsers.value
        )
    }

    @Test
    fun `should load empty contacts`() = runTest {
        stubContacts()

        setupViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.users.value.isEmpty())
        assertTrue(viewModel.filteredUsers.value.isEmpty())
    }

    @Test
    fun `should do nothing when loading contacts and current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        setupViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.users.value.isEmpty())
        assertTrue(viewModel.filteredUsers.value.isEmpty())

        verifyNoInteractions(
            contactsRepository,
            userRepository,
            conversationRepository
        )
    }

    @Test
    fun `should handle exception when loading contacts`() = runTest {
        stubFirebaseUser()

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenThrow(RuntimeException("Failed"))

        setupViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.users.value.isEmpty())
        assertTrue(viewModel.filteredUsers.value.isEmpty())
    }

    @Test
    fun `should handle exception without message when loading contacts`() = runTest {
        stubFirebaseUser()

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenThrow(RuntimeException())

        setupViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.users.value.isEmpty())
        assertTrue(viewModel.filteredUsers.value.isEmpty())
    }

    @Test
    fun `should handle contact without other user`() = runTest {
        val contact = setupContact(
            listOf(USER_1_ID)
        )

        stubContacts(
            listOf(contact)
        )

        setupViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.contacts.value.isEmpty())
        assertTrue(viewModel.users.value.isEmpty())
        assertTrue(viewModel.filteredUsers.value.isEmpty())

        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should reload contacts`() = runTest {
        val firstContact = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )
        val secondContact = setupContact(
            listOf(USER_1_ID, USER_3_ID)
        )
        val user2 = setupUser(
            userId = USER_2_ID,
            fullName = "Alice"
        )
        val user3 = setupUser(
            userId = USER_3_ID,
            fullName = "Bob"
        )

        stubFirebaseUser()

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(listOf(firstContact))
            .thenReturn(listOf(secondContact))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(user3)

        setupViewModel()

        advanceUntilIdle()

        viewModel.loadContacts()

        advanceUntilIdle()

        assertEquals(
            listOf(secondContact),
            viewModel.contacts.value
        )
        assertEquals(
            listOf(user3),
            viewModel.users.value
        )
        assertEquals(
            listOf(user3),
            viewModel.filteredUsers.value
        )
    }

    @Test
    fun `should use existing conversation`() = runTest {
        val contact = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )

        stubContacts(
            listOf(contact)
        )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(
                setupUser(
                    userId = USER_2_ID,
                    fullName = "Alice"
                )
            )

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenReturn(CONVERSATION_ID)

        setupViewModel()

        advanceUntilIdle()

        var createdConversationId: String? = null

        viewModel.createNewConversation(USER_2_ID) {
            createdConversationId = it
        }

        advanceUntilIdle()

        assertEquals(
            CONVERSATION_ID,
            createdConversationId
        )

        verify(conversationRepository).getExistingConversationId(
            listOf(
                USER_1_ID,
                USER_2_ID
            )
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
        stubContacts()

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

        setupViewModel()

        advanceUntilIdle()

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
    fun `should sort participant ids before creating conversation`() = runTest {
        val currentUserId = "9"
        val selectedUserId = "2"

        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(currentUserId)
        whenever(contactsRepository.getContacts(currentUserId))
            .thenReturn(emptyList())

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    selectedUserId,
                    currentUserId
                )
            )
        ).thenReturn(CONVERSATION_ID)

        setupViewModel()

        advanceUntilIdle()

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

        setupViewModel()

        advanceUntilIdle()

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
        stubContacts()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenThrow(RuntimeException("Failed"))

        setupViewModel()

        advanceUntilIdle()

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
        stubContacts()

        whenever(
            conversationRepository.getExistingConversationId(
                listOf(
                    USER_1_ID,
                    USER_2_ID
                )
            )
        ).thenThrow(RuntimeException())

        setupViewModel()

        advanceUntilIdle()

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
    }

    @Test
    fun `should handle exception when creating new conversation`() = runTest {
        stubContacts()

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

        setupViewModel()

        advanceUntilIdle()

        var callbackCalled = false

        viewModel.createNewConversation(USER_2_ID) {
            callbackCalled = true
        }

        advanceUntilIdle()

        assertFalse(callbackCalled)
    }

    @Test
    fun `should filter users by name ignoring case`() = runTest {
        val contact1 = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )
        val contact2 = setupContact(
            listOf(USER_1_ID, USER_3_ID)
        )
        val user2 = setupUser(
            userId = USER_2_ID,
            fullName = "Alice Smith"
        )
        val user3 = setupUser(
            userId = USER_3_ID,
            fullName = "Bob Jones"
        )

        stubContacts(
            listOf(
                contact1,
                contact2
            )
        )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(user3)

        setupViewModel()

        advanceUntilIdle()

        viewModel.filterUsers("ALICE")

        assertEquals(
            listOf(user2),
            viewModel.filteredUsers.value
        )
    }

    @Test
    fun `should return all users when filter query is empty`() = runTest {
        val contact1 = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )
        val contact2 = setupContact(
            listOf(USER_1_ID, USER_3_ID)
        )
        val user2 = setupUser(
            userId = USER_2_ID,
            fullName = "Alice"
        )
        val user3 = setupUser(
            userId = USER_3_ID,
            fullName = "Bob"
        )

        stubContacts(
            listOf(
                contact1,
                contact2
            )
        )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(user3)

        setupViewModel()

        advanceUntilIdle()

        viewModel.filterUsers("")

        assertEquals(
            listOf(
                user2,
                user3
            ),
            viewModel.filteredUsers.value
        )
    }

    @Test
    fun `should return empty users when filter does not match`() = runTest {
        val contact = setupContact(
            listOf(USER_1_ID, USER_2_ID)
        )
        val user = setupUser(
            userId = USER_2_ID,
            fullName = "Alice"
        )

        stubContacts(
            listOf(contact)
        )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user)

        setupViewModel()

        advanceUntilIdle()

        viewModel.filterUsers("Bob")

        assertTrue(
            viewModel.filteredUsers.value.isEmpty()
        )
    }

}
