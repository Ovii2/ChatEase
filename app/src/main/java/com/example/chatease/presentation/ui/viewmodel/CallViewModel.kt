package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallDirection
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.CallHistoryUiModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val callRepository: CallRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _call = MutableStateFlow<Call?>(null)
    val call = _call.asStateFlow()

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _callHistories = MutableStateFlow<List<CallHistory>>(emptyList())
    val callHistories = _callHistories.asStateFlow()

    private val _callHistoryUiModels = MutableStateFlow<List<CallHistoryUiModel>>(emptyList())
    val callHistoryUiModels = _callHistoryUiModels.asStateFlow()

    private var callObserverJob: Job? = null
    private var callHistoryObserverJob: Job? = null

    fun createCall(
        receiverId: String,
        conversationId: String,
        callType: CallType,
        onCallCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val callerId = auth.currentUser?.uid ?: return@launch
                val call = Call(
                    id = System.currentTimeMillis().toString(),
                    callerId = callerId,
                    receiverId = receiverId,
                    callType = callType,
                    status = CallStatus.CALLING,
                    conversationId = conversationId
                )
                callRepository.createCall(call)

                viewModelScope.launch {
                    callRepository.startCallTimeout(call.id)
                }

                _call.value = call
                observeCall(call.id)
                observeUser(receiverId)

                onCallCreated(call.id)
            } catch (e: Exception) {
                Log.v("CallViewModel", e.message ?: "Failed to create call")
            }
        }
    }

    fun observeCall(callId: String) {
        callObserverJob?.cancel()

        callObserverJob = viewModelScope.launch {
            callRepository.observeCall(callId)
                .collect { call ->
                    _call.value = call

                    val currentUserId = auth.currentUser?.uid ?: return@collect
                    val otherUserId = if (call?.callerId == currentUserId) {
                        call.receiverId
                    } else {
                        call?.callerId
                    }

                    if (otherUserId != null) {
                        observeUser(otherUserId)
                    }
                }
        }
    }

    fun stopObservingCall() {
        callObserverJob?.cancel()
        callObserverJob = null
    }

    fun answerCall(callId: String) {
        updateCallStatus(
            callId = callId,
            status = CallStatus.CONNECTED
        )
    }

    fun declineCall(callId: String) {
        updateCallStatus(
            callId = callId,
            status = CallStatus.DECLINED
        )
    }

    fun cancelCall(callId: String) {
        val currentCall = _call.value ?: return

        createCallHistory(
            call = currentCall,
            status = CallStatus.CANCELED
        )

        updateCallStatus(
            callId = callId,
            status = CallStatus.CANCELED
        )
    }

    fun endCall(callId: String) {
        updateCallStatus(
            callId = callId,
            status = CallStatus.ENDED
        )
    }

    fun observeCallHistory() {
        callHistoryObserverJob?.cancel()

        callHistoryObserverJob = viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            callRepository.observeCallHistory(currentUserId)
                .collect { callHistories ->

                    val uiModels = callHistories.map { callHistory ->
                        val otherUserId =
                            if (callHistory.callerId == currentUserId) callHistory.receiverId else callHistory.callerId

                        val user = userRepository.getUserById(otherUserId)

                        val callDirection = when {
                            callHistory.callerId == currentUserId -> CallDirection.OUTGOING
                            callHistory.status == CallStatus.CANCELED -> CallDirection.MISSED
                            callHistory.status == CallStatus.DECLINED -> CallDirection.MISSED
                            else -> CallDirection.INCOMING
                        }

                        CallHistoryUiModel(
                            callHistory = callHistory,
                            user = user,
                            callDirection = callDirection
                        )
                    }

                    _callHistories.value = callHistories
                    _callHistoryUiModels.value = uiModels
                }
        }
    }

    private fun createCallHistory(
        call: Call,
        status: CallStatus,
        callDuration: Long? = null
    ) {
        viewModelScope.launch {
            try {
                val callHistory = CallHistory(
                    id = UUID.randomUUID().toString(),
                    callerId = call.callerId,
                    receiverId = call.receiverId,
                    participantIds = listOf(call.callerId, call.receiverId),
                    callType = call.callType,
                    status = status,
                    timestamp = System.currentTimeMillis(),
                    callDuration = callDuration
                )
                callRepository.createCallHistory(callHistory)
            } catch (e: Exception) {
                Log.v("CallViewModel", e.message ?: "Failed to create call history")
            }
        }
    }

    private fun observeUser(userId: String) {
        viewModelScope.launch {
            userRepository.observeUser(userId)
                .collect { user ->
                    _user.value = user
                }
        }
    }

    private fun updateCallStatus(
        callId: String,
        status: CallStatus
    ) {
        viewModelScope.launch {
            try {
                callRepository.updateCallStatus(
                    callId = callId,
                    status = status
                )
            } catch (e: Exception) {
                Log.v("CallViewModel", e.message ?: "Failed to update call status")
            }
        }
    }

}
