package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.mapper.toDto
import com.example.chatease.data.remote.dto.CallHistoryDto
import com.example.chatease.data.remote.dto.IceCandidateDto
import com.example.chatease.data.remote.dto.SessionDescriptionDto
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.repository.CallRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class CallRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CallRepository {

    companion object {
        private const val CALLS = "calls"
        private const val STATUS = "status"
        private const val RECEIVER_ID = "receiverId"
        private const val CALL_HISTORY = "call_history"
        private const val PARTICIPANT_IDS = "participantIds"
        private const val CALL_TIMEOUT = 30_000L
        private const val OFFER = "offer"
        private const val ANSWER = "answer"
        private const val ICE_CANDIDATES = "ice_candidates"
        private const val CONNECTED_AT = "connectedAt"
    }

    override suspend fun createCall(call: Call) {
        firestore
            .collection(CALLS)
            .document(call.id)
            .set(call)
            .await()
    }

    override suspend fun updateCallStatus(
        callId: String,
        status: CallStatus
    ) {
        firestore
            .collection(CALLS)
            .document(callId)
            .update(STATUS, status.name)
            .await()
    }

    override fun observeCall(callId: String): Flow<Call?> = callbackFlow {
        val listener =
            firestore
                .collection(CALLS)
                .document(callId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        close(exception)
                        return@addSnapshotListener
                    }
                    val call = snapshot?.toObject(Call::class.java)
                    trySend(call)
                }
        awaitClose {
            listener.remove()
        }
    }

    override fun observeIncomingCall(userId: String): Flow<Call?> = callbackFlow {
        val listener =
            firestore
                .collection(CALLS)
                .whereEqualTo(RECEIVER_ID, userId)
                .whereEqualTo(STATUS, CallStatus.CALLING.name)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        close(exception)
                        return@addSnapshotListener
                    }

                    val incomingCall = snapshots?.toObjects(Call::class.java)?.firstOrNull()
                    trySend(incomingCall)
                }
        awaitClose {
            listener.remove()
        }
    }

    override fun observeCallHistory(userId: String): Flow<List<CallHistory>> = callbackFlow {
        val listener = firestore
            .collection(CALL_HISTORY)
            .whereArrayContains(PARTICIPANT_IDS, userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                val callHistories = snapshot
                    ?.toObjects(CallHistoryDto::class.java)
                    ?.map { it.toDomain() }.orEmpty()

                trySend(callHistories)
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun createCallHistory(callHistory: CallHistory) {
        firestore
            .collection(CALL_HISTORY)
            .document(callHistory.id)
            .set(callHistory.toDto())
            .await()
    }

    override suspend fun startCallTimeout(callId: String) {
        delay(CALL_TIMEOUT.milliseconds)

        val currentCall = firestore
            .collection(CALLS)
            .document(callId)
            .get()
            .await()

        val call = currentCall.toObject(Call::class.java)

        if (call?.status == CallStatus.CALLING) {
            updateCallStatus(callId, CallStatus.MISSED)
            createCallHistory(
                CallHistory(
                    id = UUID.randomUUID().toString(),
                    callerId = call.callerId,
                    receiverId = call.receiverId,
                    participantIds = listOf(call.callerId, call.receiverId),
                    callType = call.callType,
                    status = CallStatus.MISSED,
                    timestamp = System.currentTimeMillis(),
                    callDuration = null
                )
            )
        }
    }

    override suspend fun sendOffer(
        callId: String,
        offer: SessionDescriptionDto
    ) {
        firestore
            .collection(CALLS)
            .document(callId)
            .collection(OFFER)
            .document(OFFER)
            .set(offer)
            .await()
    }

    override suspend fun sendAnswer(
        callId: String,
        answer: SessionDescriptionDto
    ) {
        firestore
            .collection(CALLS)
            .document(callId)
            .collection(ANSWER)
            .document(ANSWER)
            .set(answer)
            .await()
    }

    override suspend fun sendIceCandidate(
        callId: String,
        candidate: IceCandidateDto
    ) {
        firestore
            .collection(CALLS)
            .document(callId)
            .collection(ICE_CANDIDATES)
            .document()
            .set(candidate)
            .await()
    }

    override fun observeOffer(callId: String): Flow<SessionDescriptionDto?> = callbackFlow {
        val listener = firestore
            .collection(CALLS)
            .document(callId)
            .collection(OFFER)
            .document(OFFER)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val description = snapshot?.toObject(SessionDescriptionDto::class.java)
                trySend(description)
            }
        awaitClose {
            listener.remove()
        }
    }

    override fun observeAnswer(callId: String): Flow<SessionDescriptionDto?> = callbackFlow {
        val listener = firestore
            .collection(CALLS)
            .document(callId)
            .collection(ANSWER)
            .document(ANSWER)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val description = snapshot?.toObject(SessionDescriptionDto::class.java)
                trySend(description)
            }
        awaitClose {
            listener.remove()
        }
    }

    override fun observeIceCandidates(callId: String): Flow<List<IceCandidateDto>> = callbackFlow {
        val listener = firestore
            .collection(CALLS)
            .document(callId)
            .collection(ICE_CANDIDATES)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val candidates = snapshots
                    ?.toObjects(IceCandidateDto::class.java).orEmpty()

                trySend(candidates)
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun updateConnectedAt(callId: String, connectedAt: Long) {
        firestore
            .collection(CALLS)
            .document(callId)
            .update(CONNECTED_AT, connectedAt)
            .await()
    }

}
