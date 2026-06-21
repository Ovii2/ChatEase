package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.mapper.toDto
import com.example.chatease.data.remote.dto.CallHistoryDto
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

}
