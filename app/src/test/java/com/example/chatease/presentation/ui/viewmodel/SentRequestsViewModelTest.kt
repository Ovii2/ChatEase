package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.ContactRequest
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ContactRequestStatus
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.SentRequestUiModel
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SentRequestsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mock()
    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val contactRequestRepository: ContactRequestRepository = mock()

    private lateinit var viewModel: SentRequestsViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val REQUEST_1_ID = "request_1"
        private const val REQUEST_2_ID = "request_2"
    }

    private fun createViewModel(): SentRequestsViewModel {
        viewModel = SentRequestsViewModel(
            userRepository = userRepository,
            auth = auth,
            contactRequestRepository = contactRequestRepository
        )
        return viewModel
    }

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun setupUser(userId: String) = User(
        uid = userId
    )

    private fun setupRequest(
        requestId: String,
        receiverUserId: String,
        status: ContactRequestStatus = ContactRequestStatus.PENDING
    ) = ContactRequest(
        id = requestId,
        senderUserId = USER_1_ID,
        receiverUserId = receiverUserId,
        timestamp = 1L,
        status = status
    )

    private fun setupSentRequest(
        requestId: String,
        receiver: User,
        status: ContactRequestStatus = ContactRequestStatus.PENDING
    ) = SentRequestUiModel(
        requestId = requestId,
        receiver = receiver,
        status = status
    )

    @Test
    fun `should load sent requests on init`() = runTest {
        val receiver2 = setupUser(USER_2_ID)
        val receiver3 = setupUser(USER_3_ID)
        val request1 = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val request2 = setupRequest(
            requestId = REQUEST_2_ID,
            receiverUserId = USER_3_ID
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(
                listOf(
                    request1,
                    request2
                )
            )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(receiver3)

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Success(
                requests = listOf(
                    setupSentRequest(
                        requestId = REQUEST_1_ID,
                        receiver = receiver2
                    ),
                    setupSentRequest(
                        requestId = REQUEST_2_ID,
                        receiver = receiver3
                    )
                )
            ),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should preserve request status when loading sent requests`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val request = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID,
            status = ContactRequestStatus.PENDING
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver)

        createViewModel()

        advanceUntilIdle()

        val state = viewModel.sentRequests.value as SentRequestsUiState.Success

        assertEquals(
            ContactRequestStatus.PENDING,
            state.requests.single().status
        )
        assertSame(
            receiver,
            state.requests.single().receiver
        )
    }

    @Test
    fun `should load empty sent requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(emptyList())

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Success(emptyList()),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should do nothing when loading sent requests and current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Loading,
            viewModel.sentRequests.value
        )

        verifyNoInteractions(
            contactRequestRepository,
            userRepository
        )
    }

    @Test
    fun `should handle exception when loading sent requests`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenThrow(RuntimeException("Failed"))

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Error("Failed"),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should use default error when loading sent requests fails without message`() = runTest {
        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenThrow(RuntimeException())

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Error("Failed to get sent requests"),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should handle exception when loading receiver`() = runTest {
        val request = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenThrow(RuntimeException("Failed"))

        createViewModel()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Error("Failed"),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should withdraw request and set empty state when last request is removed`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val request = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val sentRequest = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver)

        createViewModel()

        advanceUntilIdle()

        viewModel.withdrawContactRequest(sentRequest)

        advanceUntilIdle()

        verify(contactRequestRepository).withdrawContactRequest(
            requestId = REQUEST_1_ID,
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID
        )

        assertEquals(
            SentRequestsUiState.Empty,
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should withdraw request and keep remaining requests`() = runTest {
        val receiver2 = setupUser(USER_2_ID)
        val receiver3 = setupUser(USER_3_ID)
        val request1 = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val request2 = setupRequest(
            requestId = REQUEST_2_ID,
            receiverUserId = USER_3_ID
        )
        val sentRequest1 = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver2
        )
        val sentRequest2 = setupSentRequest(
            requestId = REQUEST_2_ID,
            receiver = receiver3
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(
                listOf(
                    request1,
                    request2
                )
            )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(receiver3)

        createViewModel()

        advanceUntilIdle()

        viewModel.withdrawContactRequest(sentRequest1)

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Success(
                listOf(sentRequest2)
            ),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should keep loading state when withdrawing outside success state`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val sentRequest = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver
        )

        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        stubFirebaseUser()

        viewModel.withdrawContactRequest(sentRequest)

        advanceUntilIdle()

        verify(contactRequestRepository).withdrawContactRequest(
            requestId = REQUEST_1_ID,
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID
        )

        assertEquals(
            SentRequestsUiState.Loading,
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should do nothing when withdrawing and current user is null`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val sentRequest = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver
        )

        whenever(auth.currentUser).thenReturn(null)

        createViewModel()

        advanceUntilIdle()

        viewModel.withdrawContactRequest(sentRequest)

        advanceUntilIdle()

        verify(
            contactRequestRepository,
            never()
        ).withdrawContactRequest(
            requestId = REQUEST_1_ID,
            senderUserId = USER_1_ID,
            receiverUserId = USER_2_ID
        )

        assertEquals(
            SentRequestsUiState.Loading,
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should handle exception when withdrawing request`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val request = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val sentRequest = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver)

        createViewModel()

        advanceUntilIdle()

        whenever(
            contactRequestRepository.withdrawContactRequest(
                requestId = REQUEST_1_ID,
                senderUserId = USER_1_ID,
                receiverUserId = USER_2_ID
            )
        ).thenThrow(RuntimeException("Failed"))

        viewModel.withdrawContactRequest(sentRequest)

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Error("Failed"),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should use default error when withdrawing request fails without message`() = runTest {
        val receiver = setupUser(USER_2_ID)
        val request = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val sentRequest = setupSentRequest(
            requestId = REQUEST_1_ID,
            receiver = receiver
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver)

        createViewModel()

        advanceUntilIdle()

        whenever(
            contactRequestRepository.withdrawContactRequest(
                requestId = REQUEST_1_ID,
                senderUserId = USER_1_ID,
                receiverUserId = USER_2_ID
            )
        ).thenThrow(RuntimeException())

        viewModel.withdrawContactRequest(sentRequest)

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Error("Failed to withdraw request"),
            viewModel.sentRequests.value
        )
    }

    @Test
    fun `should reload sent requests`() = runTest {
        val receiver2 = setupUser(USER_2_ID)
        val receiver3 = setupUser(USER_3_ID)
        val request1 = setupRequest(
            requestId = REQUEST_1_ID,
            receiverUserId = USER_2_ID
        )
        val request2 = setupRequest(
            requestId = REQUEST_2_ID,
            receiverUserId = USER_3_ID
        )

        stubFirebaseUser()

        whenever(contactRequestRepository.getSentRequests(USER_1_ID))
            .thenReturn(listOf(request1))
            .thenReturn(listOf(request2))

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(receiver2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(receiver3)

        createViewModel()

        advanceUntilIdle()

        viewModel.loadSentRequests()

        advanceUntilIdle()

        assertEquals(
            SentRequestsUiState.Success(
                listOf(
                    setupSentRequest(
                        requestId = REQUEST_2_ID,
                        receiver = receiver3
                    )
                )
            ),
            viewModel.sentRequests.value
        )
    }

}
