package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.ContactRequestCooldown
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.CooldownUiModel
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val userRepository: UserRepository = mock()
    private val contactRequestRepository: ContactRequestRepository = mock()
    private val contactsRepository: ContactsRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: ContactsViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val REQUEST_ID = "1"
        private const val CONTACT_ID = "1"
        private const val SEARCH_VALUE = "Test"
    }

    @Before
    fun setUp() {
        viewModel = ContactsViewModel(
            userRepository = userRepository,
            auth = auth,
            contactRequestRepository = contactRequestRepository,
            contactsRepository = contactsRepository
        )
    }

    private fun setupUser(userId: String) = User(
        uid = userId,
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList(),
        membership = Membership.FREE
    )

    private fun setupContactRequest(status: ContactRequestStatus) = ContactRequest(
        id = REQUEST_ID,
        senderUserId = USER_1_ID,
        receiverUserId = USER_2_ID,
        timestamp = System.currentTimeMillis(),
        status = status
    )

    private fun setupContact() = Contact(
        id = CONTACT_ID,
        userIds = listOf(USER_1_ID, USER_2_ID),
        createdAt = System.currentTimeMillis()
    )

    private fun setupCooldown(userId: String, expiresAt: Long) = CooldownUiModel(
        userId = userId,
        expiresAt = expiresAt
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
    fun `should return null when current user is null`() {
        whenever(auth.currentUser).thenReturn(null)

        assertEquals(null, viewModel.currentUserId)
    }

    @Test
    fun `should update search value`() = runTest {
        viewModel.onSearchValueChange(SEARCH_VALUE)

        assertEquals(SEARCH_VALUE, viewModel.searchValue.value)
    }

    @Test
    fun `should clear search`() = runTest {
        viewModel.onSearchValueChange(SEARCH_VALUE)

        viewModel.clearSearch()

        assertEquals("", viewModel.searchValue.value)
    }

    @Test
    fun `should search users`() = runTest {
        stubFirebaseUser()
        whenever(userRepository.searchUsers(SEARCH_VALUE))
            .thenReturn(listOf(setupUser(USER_2_ID)))

        viewModel.searchUsers(SEARCH_VALUE)

        advanceUntilIdle()
        verify(userRepository).searchUsers(SEARCH_VALUE)
    }

    @Test
    fun `should handle exception when searching users`() = runTest {
        stubFirebaseUser()

        whenever(userRepository.searchUsers(SEARCH_VALUE))
            .thenThrow(RuntimeException())

        viewModel.searchUsers(SEARCH_VALUE)

        advanceUntilIdle()
        assertEquals(emptyList<User>(), viewModel.searchedUsers.value)
        assertEquals(false, viewModel.isSearching.value)
    }

    @Test
    fun `should send contact request`() = runTest {
        stubFirebaseUser()

        viewModel.sendContactRequest(USER_2_ID)

        advanceUntilIdle()
        assertEquals(listOf(USER_2_ID), viewModel.sentRequests.value)
    }

    @Test
    fun `should handle exception when sending contact request`() = runTest {
        stubFirebaseUser()
        whenever(
            contactRequestRepository.sendContactRequest(
                senderUserId = USER_1_ID,
                receiverUserId = USER_2_ID
            )
        ).thenThrow(RuntimeException())

        viewModel.sendContactRequest(USER_2_ID)

        advanceUntilIdle()
        verify(contactRequestRepository).sendContactRequest(
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID
        )
    }

    @Test
    fun `should get pending requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getPendingRequests(USER_1_ID))
            .thenReturn(listOf(setupContactRequest(ContactRequestStatus.PENDING)))
        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        viewModel.getPendingRequests()

        advanceUntilIdle()
        verify(contactRequestRepository).getPendingRequests(USER_1_ID)
        verify(userRepository).getUserById(USER_1_ID)
    }

    @Test
    fun `should handle exception when getting pending requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getPendingRequests(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.getPendingRequests()

        advanceUntilIdle()
        verify(contactRequestRepository).getPendingRequests(USER_1_ID)
    }

    @Test
    fun `should get sent requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(setupContactRequest(ContactRequestStatus.PENDING)))

        viewModel.getSentRequests()

        advanceUntilIdle()
        verify(contactRequestRepository).getSentRequests(USER_1_ID)
    }

    @Test
    fun `should handle exception when getting sent requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenThrow(RuntimeException())

        viewModel.getSentRequests()

        advanceUntilIdle()
        verify(contactRequestRepository).getSentRequests(USER_1_ID)
    }

    @Test
    fun `should accept contact request`() = runTest {
        stubFirebaseUser()

        val request = setupContactRequest(ContactRequestStatus.PENDING)

        whenever(contactRequestRepository.getPendingRequests(USER_1_ID))
            .thenReturn(listOf(request))
            .thenReturn(emptyList())

        whenever(userRepository.getUserById(request.senderUserId))
            .thenReturn(setupUser(request.senderUserId))

        viewModel.getPendingRequests()
        advanceUntilIdle()

        viewModel.acceptContactRequest(REQUEST_ID)
        advanceUntilIdle()

        assertEquals(
            emptyList<PendingRequestUiModel>(),
            viewModel.pendingRequests.value
        )
    }

    @Test
    fun `should handle exception when accepting contact request`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.acceptContactRequest(REQUEST_ID))
            .thenThrow(RuntimeException())

        viewModel.acceptContactRequest(REQUEST_ID)

        advanceUntilIdle()
        verify(contactRequestRepository).acceptContactRequest(REQUEST_ID)
    }

    @Test
    fun `should decline contact request`() = runTest {
        viewModel.declineContactRequest(REQUEST_ID)

        advanceUntilIdle()
        verify(contactRequestRepository).declineContactRequest(REQUEST_ID)
    }

    @Test
    fun `should handle exception when declining contact request`() = runTest {
        whenever(contactRequestRepository.declineContactRequest(REQUEST_ID))
            .thenThrow(RuntimeException())

        viewModel.declineContactRequest(REQUEST_ID)

        advanceUntilIdle()
        verify(contactRequestRepository).declineContactRequest(REQUEST_ID)
    }

    @Test
    fun `should get contacts`() = runTest {
        stubFirebaseUser()

        val contacts = listOf(setupContact())

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(contacts)
        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn((setupUser(USER_2_ID)))
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        viewModel.getContacts()

        advanceUntilIdle()
        assertEquals(listOf(setupUser(USER_2_ID)), viewModel.contacts.value)
        verify(contactsRepository).getContacts(USER_1_ID)
        verify(userRepository).getUserById(USER_2_ID)
    }

    @Test
    fun `should load cooldown user ids`() = runTest {
        stubFirebaseUser()
        val cooldown = ContactRequestCooldown(
            id = REQUEST_ID,
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID,
            withdrawnAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + 60_000L,
            timeLeft = 60_000L
        )
        whenever(userRepository.searchUsers(SEARCH_VALUE))
            .thenReturn(listOf(setupUser(USER_2_ID)))

        whenever(
            contactRequestRepository.getCooldown(
                senderUserId = USER_1_ID,
                receiverUserId = USER_2_ID
            )
        ).thenReturn(
            cooldown
        )

        viewModel.searchUsers(SEARCH_VALUE)
        advanceUntilIdle()

        assertEquals(
            listOf(
                CooldownUiModel(
                    userId = USER_2_ID,
                    expiresAt = cooldown.expiresAt
                )
            ),
            viewModel.cooldowns.value
        )
    }

    @Test
    fun `should handle exception when loading cooldown user ids`() = runTest {
        stubFirebaseUser()

        whenever(userRepository.searchUsers(SEARCH_VALUE))
            .thenReturn(listOf(setupUser(USER_2_ID)))
        whenever(
            contactRequestRepository.getCooldown(
                senderUserId = USER_1_ID,
                receiverUserId = USER_2_ID
            )
        ).thenThrow(RuntimeException())

        viewModel.searchUsers(SEARCH_VALUE)

        advanceUntilIdle()
        assertEquals(
            emptyList<CooldownUiModel>(),
            viewModel.cooldowns.value
        )
        verify(contactRequestRepository).getCooldown(
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID
        )
    }

    @Test
    fun `should observe pending requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.observePendingRequests(USER_1_ID))
            .thenReturn(flowOf(listOf(setupContactRequest(ContactRequestStatus.PENDING))))
        whenever(userRepository.getUserById(USER_1_ID))
            .thenReturn(setupUser(USER_1_ID))

        viewModel = ContactsViewModel(
            userRepository = userRepository,
            auth = auth,
            contactRequestRepository = contactRequestRepository,
            contactsRepository = contactsRepository
        )

        advanceUntilIdle()
        assertEquals(
            listOf(
                PendingRequestUiModel(
                    requestId = REQUEST_ID,
                    user = setupUser(USER_1_ID)
                )
            ),
            viewModel.pendingRequests.value
        )
        verify(userRepository).getUserById(USER_1_ID)
    }

    @Test
    fun `should observe contact users`() = runTest {
        stubFirebaseUser()

        whenever(contactsRepository.getContacts(USER_1_ID))
            .thenReturn(listOf(setupContact()))
        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(setupUser(USER_2_ID))
        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        viewModel.getContacts()

        advanceUntilIdle()
        assertEquals(listOf(setupUser(USER_2_ID)), viewModel.contacts.value)
    }

}