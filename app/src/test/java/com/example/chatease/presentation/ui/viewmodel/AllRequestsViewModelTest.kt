package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.presentation.ui.model.SentRequestUiModel
import com.example.chatease.presentation.ui.state.ReceivedRequestsUiState
import com.example.chatease.presentation.ui.state.SentRequestsUiState
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
class AllRequestsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val userRepository: UserRepository = mock()
    private val contactRequestRepository: ContactRequestRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: AllRequestsViewModel

    companion object {
        private const val REQUEST_ID = "1"
        private const val SENDER_USER_ID = "1"
        private const val RECEIVER_USER_ID = "2"
    }

    @Before
    fun setUp() {
        viewModel = AllRequestsViewModel(
            auth = auth,
            userRepository = userRepository,
            contactRequestRepository = contactRequestRepository
        )
    }

    private fun setupUser(userId: String): User = User(
        uid = userId,
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList(),
        membership = Membership.FREE
    )

    private fun setupSentRequest(): SentRequestUiModel {
        val receiver = setupUser(RECEIVER_USER_ID)
        return SentRequestUiModel(
            requestId = REQUEST_ID,
            receiver = receiver,
            status = ContactRequestStatus.PENDING
        )
    }

    private fun setupContactRequest(): ContactRequest = ContactRequest(
        id = REQUEST_ID,
        senderUserId = SENDER_USER_ID,
        receiverUserId = RECEIVER_USER_ID,
        timestamp = System.currentTimeMillis(),
        status = ContactRequestStatus.PENDING
    )

    @Test
    fun `should accept contact request`() = runTest {
        viewModel.acceptContactRequest(REQUEST_ID)

        advanceUntilIdle()
        verify(contactRequestRepository).acceptContactRequest(REQUEST_ID)
    }

    @Test
    fun `should handle exception when accepting contact request`() = runTest {
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
    fun `should withdraw contact request`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)

        val request = setupSentRequest()

        viewModel.withDrawContactRequest(request)

        advanceUntilIdle()
        verify(contactRequestRepository).withdrawContactRequest(
            requestId = REQUEST_ID,
            senderUserId = SENDER_USER_ID,
            receiverUserId = request.receiver.uid
        )
    }

    @Test
    fun `should handle exception when withdrawing contact request`() = runTest {
        val request = setupSentRequest()

        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)
        whenever(
            contactRequestRepository.withdrawContactRequest(
                requestId = REQUEST_ID,
                senderUserId = SENDER_USER_ID,
                receiverUserId = request.receiver.uid
            )
        ).thenThrow(RuntimeException())


        viewModel.withDrawContactRequest(request)

        advanceUntilIdle()
        verify(contactRequestRepository).withdrawContactRequest(
            requestId = REQUEST_ID,
            senderUserId = SENDER_USER_ID,
            receiverUserId = request.receiver.uid
        )
    }

    @Test
    fun `should observe received requests`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)

        whenever(contactRequestRepository.observePendingRequests(SENDER_USER_ID))
            .thenReturn(flowOf(listOf(setupContactRequest())))

        whenever(userRepository.getUserById(SENDER_USER_ID))
            .thenReturn(setupUser(SENDER_USER_ID))

        whenever(contactRequestRepository.observeSentRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))

        val viewModel = AllRequestsViewModel(
            auth = auth,
            userRepository = userRepository,
            contactRequestRepository = contactRequestRepository
        )

        advanceUntilIdle()
        assertEquals(
            ReceivedRequestsUiState.Success(
                requests = listOf(
                    PendingRequestUiModel(
                        requestId = REQUEST_ID,
                        user = setupUser(SENDER_USER_ID)
                    )
                )
            ),
            viewModel.receivedRequests.value
        )
    }

    @Test
    fun `should set received requests to empty when there are no requests`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)

        whenever(contactRequestRepository.observePendingRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))

        whenever(contactRequestRepository.observeSentRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))

        val viewModel = AllRequestsViewModel(
            auth = auth,
            userRepository = userRepository,
            contactRequestRepository = contactRequestRepository
        )

        advanceUntilIdle()
        assertEquals(
            ReceivedRequestsUiState.Empty,
            viewModel.receivedRequests.value
        )
    }

    @Test
    fun `should observe sent requests`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)

        whenever(contactRequestRepository.observeSentRequests(SENDER_USER_ID))
            .thenReturn(flowOf(listOf(setupContactRequest())))

        whenever(userRepository.getUserById(RECEIVER_USER_ID))
            .thenReturn(setupUser(RECEIVER_USER_ID))

        whenever(contactRequestRepository.observePendingRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))


        val viewModel = AllRequestsViewModel(
            auth = auth,
            userRepository = userRepository,
            contactRequestRepository = contactRequestRepository
        )

        advanceUntilIdle()
        assertEquals(
            SentRequestsUiState.Success(
                requests = listOf(
                    SentRequestUiModel(
                        requestId = REQUEST_ID,
                        receiver = setupUser(RECEIVER_USER_ID),
                        status = ContactRequestStatus.PENDING
                    )
                )
            ), viewModel.sentRequests.value
        )
    }

    @Test
    fun `should set sent requests to empty when there are no requests`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(SENDER_USER_ID)

        whenever(contactRequestRepository.observePendingRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))

        whenever(contactRequestRepository.observeSentRequests(SENDER_USER_ID))
            .thenReturn(flowOf(emptyList()))

        val viewModel = AllRequestsViewModel(
            auth = auth,
            userRepository = userRepository,
            contactRequestRepository = contactRequestRepository
        )

        advanceUntilIdle()
        assertEquals(
            SentRequestsUiState.Empty,
            viewModel.sentRequests.value
        )
    }

}