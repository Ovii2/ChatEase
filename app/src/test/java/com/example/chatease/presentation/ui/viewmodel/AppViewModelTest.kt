package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.FcmTokenProvider
import com.example.chatease.domain.repository.UserRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val callRepository: CallRepository = mock()
    private val userRepository: UserRepository = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val fcmTokenProvider: FcmTokenProvider = mock()
    private lateinit var viewModel: AppViewModel

    companion object {
        private const val USER_ID = "1"
        private const val CALL_ID = "1"
        private const val RECEIVER_ID = "2"
        private const val CONVERSATION_ID = "1"
        private const val TEST_TOKEN = "test_token"
    }

    @Before
    fun setUp() {
        viewModel = AppViewModel(
            auth = auth,
            callRepository = callRepository,
            userRepository = userRepository,
            fcmTokenProvider = fcmTokenProvider,
        )
    }

    private fun setupCall(status: CallStatus) = Call(
        id = CALL_ID,
        callerId = USER_ID,
        receiverId = RECEIVER_ID,
        callType = CallType.AUDIO,
        status = status,
        conversationId = CONVERSATION_ID,
        connectedAt = System.currentTimeMillis()
    )

    @Test
    fun `should observe incoming call`() = runTest {
        val incomingCall = setupCall(CallStatus.INCOMING)

        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_ID)
        whenever(callRepository.observeIncomingCall(USER_ID))
            .thenReturn(flowOf(incomingCall))

        viewModel.observeIncomingCall()

        advanceUntilIdle()
        assertEquals(incomingCall, viewModel.incomingCall.value)
    }

    @Test
    fun `should save current fcm token`() = runTest {
        whenever(fcmTokenProvider.getToken()).thenReturn(TEST_TOKEN)

        viewModel.saveCurrentFcmToken()

        advanceUntilIdle()
        verify(userRepository).saveFcmToken(TEST_TOKEN)
    }

    @Test
    fun `should handle exception when saving current fcm token`() = runTest {
        whenever(userRepository.saveFcmToken(TEST_TOKEN))
            .thenThrow(RuntimeException())
        whenever(fcmTokenProvider.getToken()).thenReturn(TEST_TOKEN)

        viewModel.saveCurrentFcmToken()

        advanceUntilIdle()
        verify(userRepository).saveFcmToken(any())
    }

}