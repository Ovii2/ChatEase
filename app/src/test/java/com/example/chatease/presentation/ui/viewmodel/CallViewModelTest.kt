package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.data.mapper.toDto
import com.example.chatease.data.webrtc.WebRtcClient
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.CallHistoryUiModel
import com.example.chatease.presentation.ui.state.CallsUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

@OptIn(ExperimentalCoroutinesApi::class)
class CallViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val auth: FirebaseAuth = mock()
    private val callRepository: CallRepository = mock()
    private val userRepository: UserRepository = mock()
    private val webRtcClient: WebRtcClient = mock()
    private val firebaseUser: FirebaseUser = mock()
    private lateinit var viewModel: CallViewModel

    companion object {
        private const val USER_1_ID = "1"
        private const val USER_2_ID = "2"
        private const val USER_3_ID = "3"
        private const val USER_4_ID = "4"
        private const val CALL_ID = "1"
        private const val CONVERSATION_ID = "1"
    }

    @Before
    fun setUp() {
        viewModel = CallViewModel(
            auth = auth,
            callRepository = callRepository,
            userRepository = userRepository,
            webRtcClient = webRtcClient
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

    private fun setupCall(
        status: CallStatus = CallStatus.INCOMING,
        callerId: String = USER_1_ID,
        receiverId: String = USER_2_ID,
        connectedAt: Long? = null
    ) = Call(
        id = CALL_ID,
        callerId = callerId,
        receiverId = receiverId,
        callType = CallType.AUDIO,
        status = status,
        conversationId = CONVERSATION_ID,
        connectedAt = connectedAt
    )

    private fun setupCallHistory(
        id: String,
        callerId: String,
        receiverId: String,
        status: CallStatus
    ) = CallHistory(
        id = id,
        ownerId = receiverId,
        callerId = callerId,
        receiverId = receiverId,
        participantIds = listOf(callerId, receiverId),
        callType = CallType.AUDIO,
        status = status,
        timestamp = System.currentTimeMillis(),
        callDuration = null
    )

    private fun stubFirebaseUser() {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_1_ID)
    }

    private fun stubObservedCall(
        call: Call = setupCall()
    ) {
        stubFirebaseUser()

        whenever(callRepository.observeCall(CALL_ID))
            .thenReturn(flowOf(call))

        whenever(userRepository.observeUser(call.receiverId))
            .thenReturn(flowOf(setupUser(call.receiverId)))
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
    fun `should create call`() = runTest {
        stubFirebaseUser()

        val answer = SessionDescription(
            SessionDescription.Type.ANSWER,
            "answer_sdp"
        )

        val remoteCandidate = IceCandidate(
            "audio",
            0,
            "remote_candidate"
        )

        whenever(callRepository.observeIceCandidates(any()))
            .thenReturn(
                flowOf(
                    listOf(remoteCandidate.toDto())
                )
            )

        whenever(callRepository.observeAnswer(any()))
            .thenReturn(
                flowOf(answer.toDto())
            )

        whenever(callRepository.observeCall(any()))
            .thenReturn(emptyFlow())

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(emptyFlow())

        var createdCallId: String? = null

        viewModel.createCall(
            receiverId = USER_2_ID,
            conversationId = CONVERSATION_ID,
            callType = CallType.AUDIO,
            onCallCreated = { callId ->
                createdCallId = callId
            }
        )

        advanceUntilIdle()

        val callCaptor = argumentCaptor<Call>()

        verify(callRepository).createCall(callCaptor.capture())

        val createdCall = callCaptor.firstValue

        assertEquals(USER_1_ID, createdCall.callerId)
        assertEquals(USER_2_ID, createdCall.receiverId)
        assertEquals(CONVERSATION_ID, createdCall.conversationId)
        assertEquals(CallType.AUDIO, createdCall.callType)
        assertEquals(CallStatus.CALLING, createdCall.status)

        assertEquals(createdCall, viewModel.call.value)
        assertEquals(createdCall.id, createdCallId)

        verify(webRtcClient).initializeAudio()
        verify(webRtcClient).createPeerConnection()

        verify(callRepository).startCallTimeout(createdCall.id)

        verify(callRepository).updateConnectedAt(
            org.mockito.kotlin.eq(createdCall.id),
            any()
        )
    }

    @Test
    fun `should observe call`() = runTest {
        val call = setupCall()
        val receiver = setupUser(USER_2_ID)

        stubFirebaseUser()

        whenever(callRepository.observeCall(CALL_ID))
            .thenReturn(flowOf(call))

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(receiver))

        viewModel.observeCall(CALL_ID)

        advanceUntilIdle()

        assertEquals(call, viewModel.call.value)
        assertEquals(receiver, viewModel.user.value)
    }

    @Test
    fun `should stop observing call`() = runTest {
        val calls = MutableSharedFlow<Call?>()

        val firstCall = setupCall(CallStatus.INCOMING)
        val secondCall = setupCall(CallStatus.ENDED)

        stubFirebaseUser()

        whenever(callRepository.observeCall(CALL_ID))
            .thenReturn(calls)

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(flowOf(setupUser(USER_2_ID)))

        viewModel.observeCall(CALL_ID)

        runCurrent()

        calls.emit(firstCall)

        runCurrent()

        viewModel.stopObservingCall()

        calls.emit(secondCall)

        runCurrent()

        assertEquals(firstCall, viewModel.call.value)
    }

    @Test
    fun `should answer call`() = runTest {
        val offer = SessionDescription(
            SessionDescription.Type.OFFER,
            "test_offer"
        )

        val answer = SessionDescription(
            SessionDescription.Type.ANSWER,
            "test_answer"
        )

        whenever(callRepository.observeOffer(CALL_ID))
            .thenReturn(flowOf(offer.toDto()))

        whenever(callRepository.observeIceCandidates(CALL_ID))
            .thenReturn(emptyFlow())

        val answerCaptor = argumentCaptor<(SessionDescription) -> Unit>()

        viewModel.answerCall(CALL_ID)

        advanceUntilIdle()

        verify(webRtcClient).initializeAudio()
        verify(webRtcClient).createPeerConnection()
        verify(webRtcClient).setRemoteDescription(any())

        verify(webRtcClient).createAnswer(answerCaptor.capture())

        answerCaptor.firstValue(answer)

        advanceUntilIdle()

        verify(callRepository).sendAnswer(
            callId = CALL_ID,
            answer = answer.toDto()
        )

        verify(callRepository).updateCallStatus(
            callId = CALL_ID,
            status = CallStatus.CONNECTED
        )

        verify(callRepository).updateConnectedAt(
            eq(CALL_ID),
            any()
        )
    }

    @Test
    fun `should send ice candidate when answering call`() = runTest {
        val offer = SessionDescription(
            SessionDescription.Type.OFFER,
            "test_offer"
        )

        whenever(callRepository.observeOffer(CALL_ID))
            .thenReturn(flowOf(offer.toDto()))

        whenever(callRepository.observeIceCandidates(CALL_ID))
            .thenReturn(emptyFlow())

        val iceCandidateCaptor = argumentCaptor<(IceCandidate) -> Unit>()

        viewModel.answerCall(CALL_ID)

        advanceUntilIdle()

        verify(webRtcClient)
            .setOnIceCandidateCreatedListener(iceCandidateCaptor.capture())

        val candidate = IceCandidate(
            "audio",
            0,
            "test_candidate"
        )

        iceCandidateCaptor.firstValue(candidate)

        advanceUntilIdle()

        verify(callRepository).sendIceCandidate(
            callId = eq(CALL_ID),
            candidate = eq(candidate.toDto())
        )
    }

    @Test
    fun `should not create call when current user is null`() = runTest {
        whenever(auth.currentUser).thenReturn(null)

        viewModel.createCall(
            receiverId = USER_2_ID,
            conversationId = CONVERSATION_ID,
            callType = CallType.AUDIO,
            onCallCreated = {}
        )

        advanceUntilIdle()

        verify(callRepository, never()).createCall(any())
    }

    @Test
    fun `should decline call`() = runTest {
        stubObservedCall()

        viewModel.observeCall(CALL_ID)
        advanceUntilIdle()

        viewModel.declineCall(CALL_ID)
        advanceUntilIdle()

        verify(webRtcClient).endCall()

        verify(callRepository).updateCallStatus(
            callId = CALL_ID,
            status = CallStatus.DECLINED
        )

        val historyCaptor = argumentCaptor<CallHistory>()

        verify(callRepository).createCallHistory(historyCaptor.capture())

        assertEquals(
            CallStatus.DECLINED,
            historyCaptor.firstValue.status
        )
    }

    @Test
    fun `should cancel call`() = runTest {
        stubObservedCall()

        viewModel.observeCall(CALL_ID)
        advanceUntilIdle()

        viewModel.cancelCall(CALL_ID)
        advanceUntilIdle()

        verify(webRtcClient).endCall()

        verify(callRepository).updateCallStatus(
            callId = CALL_ID,
            status = CallStatus.CANCELED
        )

        val historyCaptor = argumentCaptor<CallHistory>()

        verify(callRepository).createCallHistory(historyCaptor.capture())

        assertEquals(
            CallStatus.CANCELED,
            historyCaptor.firstValue.status
        )
    }

    @Test
    fun `should end call`() = runTest {
        val call = setupCall(
            status = CallStatus.CONNECTED,
            connectedAt = System.currentTimeMillis() - 1_000
        )

        stubObservedCall(call)

        viewModel.observeCall(CALL_ID)
        advanceUntilIdle()

        viewModel.endCall(CALL_ID)
        advanceUntilIdle()

        verify(webRtcClient).endCall()

        verify(callRepository).updateCallStatus(
            callId = CALL_ID,
            status = CallStatus.ENDED
        )

        val historyCaptor = argumentCaptor<CallHistory>()

        verify(callRepository).createCallHistory(historyCaptor.capture())

        assertEquals(
            CallStatus.ENDED,
            historyCaptor.firstValue.status
        )
    }

    @Test
    fun `should send ice candidate when created`() = runTest {
        stubFirebaseUser()

        val answer = SessionDescription(
            SessionDescription.Type.ANSWER,
            "test_answer"
        )

        whenever(callRepository.observeIceCandidates(any()))
            .thenReturn(emptyFlow())

        whenever(callRepository.observeAnswer(any()))
            .thenReturn(flowOf(answer.toDto()))

        whenever(callRepository.observeCall(any()))
            .thenReturn(emptyFlow())

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(emptyFlow())

        val listenerCaptor = argumentCaptor<(IceCandidate) -> Unit>()

        viewModel.createCall(
            receiverId = USER_2_ID,
            conversationId = CONVERSATION_ID,
            callType = CallType.AUDIO,
            onCallCreated = {}
        )

        advanceUntilIdle()

        verify(webRtcClient)
            .setOnIceCandidateCreatedListener(listenerCaptor.capture())

        val candidate = IceCandidate(
            "audio",
            0,
            "test_candidate"
        )

        listenerCaptor.firstValue(candidate)

        advanceUntilIdle()

        verify(callRepository).sendIceCandidate(
            callId = any(),
            candidate = eq(candidate.toDto())
        )
    }

    @Test
    fun `should send offer when created`() = runTest {
        stubFirebaseUser()

        val answer = SessionDescription(
            SessionDescription.Type.ANSWER,
            "test_answer"
        )

        whenever(callRepository.observeIceCandidates(any()))
            .thenReturn(emptyFlow())

        whenever(callRepository.observeAnswer(any()))
            .thenReturn(flowOf(answer.toDto()))

        whenever(callRepository.observeCall(any()))
            .thenReturn(emptyFlow())

        whenever(userRepository.observeUser(USER_2_ID))
            .thenReturn(emptyFlow())

        val offerCaptor = argumentCaptor<(SessionDescription) -> Unit>()

        viewModel.createCall(
            receiverId = USER_2_ID,
            conversationId = CONVERSATION_ID,
            callType = CallType.AUDIO,
            onCallCreated = {}
        )

        advanceUntilIdle()

        verify(webRtcClient).createOffer(offerCaptor.capture())

        val offer = SessionDescription(
            SessionDescription.Type.OFFER,
            "test_offer"
        )

        offerCaptor.firstValue(offer)

        advanceUntilIdle()

        verify(callRepository).sendOffer(
            callId = any(),
            eq(offer.toDto())
        )
    }

    @Test
    fun `should handle exception when creating call`() = runTest {
        stubFirebaseUser()

        whenever(callRepository.createCall(any()))
            .thenThrow(RuntimeException())

        viewModel.createCall(
            receiverId = USER_2_ID,
            conversationId = CONVERSATION_ID,
            callType = CallType.AUDIO,
            onCallCreated = {}
        )

        advanceUntilIdle()

        verify(callRepository).createCall(any())
        verify(webRtcClient, never()).initializeAudio()
        verify(webRtcClient, never()).createPeerConnection()
    }

    @Test
    fun `should observe call history`() = runTest {
        stubFirebaseUser()

        val outgoingHistory = setupCallHistory(
            id = "1",
            callerId = USER_1_ID,
            receiverId = USER_2_ID,
            status = CallStatus.ENDED
        )

        val missedHistory = setupCallHistory(
            id = "2",
            callerId = USER_3_ID,
            receiverId = USER_1_ID,
            status = CallStatus.CANCELED
        )

        val incomingHistory = setupCallHistory(
            id = "3",
            callerId = USER_4_ID,
            receiverId = USER_1_ID,
            status = CallStatus.ENDED
        )

        val user2 = setupUser(USER_2_ID)
        val user3 = setupUser(USER_3_ID)
        val user4 = setupUser(USER_4_ID)

        whenever(callRepository.observeCallHistory(USER_1_ID))
            .thenReturn(
                flowOf(
                    listOf(
                        outgoingHistory,
                        missedHistory,
                        incomingHistory
                    )
                )
            )

        whenever(userRepository.getUserById(USER_2_ID))
            .thenReturn(user2)

        whenever(userRepository.getUserById(USER_3_ID))
            .thenReturn(user3)

        whenever(userRepository.getUserById(USER_4_ID))
            .thenReturn(user4)

        viewModel.observeCallHistory()

        advanceUntilIdle()

        assertEquals(
            CallsUiState.Success(
                callHistoryList = listOf(
                    CallHistoryUiModel(
                        callHistory = outgoingHistory,
                        user = user2,
                        callDirection = CallDirection.OUTGOING
                    ),
                    CallHistoryUiModel(
                        callHistory = missedHistory,
                        user = user3,
                        callDirection = CallDirection.MISSED
                    ),
                    CallHistoryUiModel(
                        callHistory = incomingHistory,
                        user = user4,
                        callDirection = CallDirection.INCOMING
                    )
                )
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should toggle speaker`() {
        viewModel.toggleSpeaker()

        assertEquals(true, viewModel.isSpeakerEnabled.value)

        verify(webRtcClient).setSpeakerEnabled(true)
    }

    @Test
    fun `should disable speaker when toggled twice`() {
        viewModel.toggleSpeaker()
        viewModel.toggleSpeaker()

        assertEquals(false, viewModel.isSpeakerEnabled.value)

        verify(webRtcClient).setSpeakerEnabled(false)
    }

    @Test
    fun `should toggle mute`() {
        viewModel.toggleMute()

        assertEquals(true, viewModel.isMuted.value)

        verify(webRtcClient).setMuted(true)
    }

    @Test
    fun `should disable mute when toggled twice`() {
        viewModel.toggleMute()
        viewModel.toggleMute()

        assertEquals(false, viewModel.isMuted.value)

        verify(webRtcClient).setMuted(false)
    }

    @Test
    fun `should clean up call`() {
        viewModel.cleanUpCall()

        verify(webRtcClient).endCall()
    }

    @Test
    fun `should observe missed calls count`() = runTest {
        stubFirebaseUser()

        whenever(callRepository.observeMissedCallsCount(USER_1_ID))
            .thenReturn(flowOf(3))

        viewModel.observeMissedCallsCount()

        advanceUntilIdle()

        assertEquals(3, viewModel.missedCallsCount.value)
    }

    @Test
    fun `should mark missed calls as seen`() = runTest {
        stubFirebaseUser()

        viewModel.markMissedCallsAsSeen()

        advanceUntilIdle()

        verify(callRepository).markMissedCallsAsSeen(USER_1_ID)
    }

    @Test
    fun `should handle exception when marking missed calls as seen`() = runTest {
        stubFirebaseUser()

        whenever(
            callRepository.markMissedCallsAsSeen(USER_1_ID)
        ).thenThrow(RuntimeException())

        viewModel.markMissedCallsAsSeen()

        advanceUntilIdle()

        verify(callRepository).markMissedCallsAsSeen(USER_1_ID)
    }

    @Test
    fun `should handle exception when creating call history`() = runTest {
        stubObservedCall()

        whenever(callRepository.createCallHistory(any()))
            .thenThrow(RuntimeException())

        viewModel.observeCall(CALL_ID)
        advanceUntilIdle()

        viewModel.declineCall(CALL_ID)

        advanceUntilIdle()

        verify(callRepository).createCallHistory(any())
    }

    @Test
    fun `should handle exception when updating call status`() = runTest {
        stubObservedCall()

        whenever(
            callRepository.updateCallStatus(
                callId = CALL_ID,
                status = CallStatus.DECLINED
            )
        ).thenThrow(RuntimeException())

        viewModel.observeCall(CALL_ID)
        advanceUntilIdle()

        viewModel.declineCall(CALL_ID)

        advanceUntilIdle()

        verify(callRepository).updateCallStatus(
            callId = CALL_ID,
            status = CallStatus.DECLINED
        )
    }

}
