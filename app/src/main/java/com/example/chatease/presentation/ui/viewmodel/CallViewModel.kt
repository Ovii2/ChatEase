package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.CallStatus
import com.example.chatease.domain.model.enums.CallType
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private var callObserverJob: Job? = null

    fun createCall(receiverId: String, callType: CallType) {
        viewModelScope.launch {
            try {
                val callerId = auth.currentUser?.uid ?: return@launch
                val call = Call(
                    id = System.currentTimeMillis().toString(),
                    callerId = callerId,
                    receiverId = receiverId,
                    callType = callType,
                    status = CallStatus.CALLING
                )
                callRepository.createCall(call)
                _call.value = call
                observeCall(call.id)
                observeUser(receiverId)
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
                }
        }
    }

    fun stopObservingCall() {
        callObserverJob?.cancel()
        callObserverJob = null
    }

    fun observeIncomingCall(userId: String) {
        callObserverJob?.cancel()

        callObserverJob = viewModelScope.launch {
            callRepository.observeIncomingCall(userId)
                .collect { call ->
                    _call.value = call
                }
        }
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
