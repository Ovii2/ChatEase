package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.presentation.ui.model.SentRequestUiModel
import com.example.chatease.presentation.ui.state.ReceivedRequestsUiState
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllRequestsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val contactRequestRepository: ContactRequestRepository
) : ViewModel() {

    private val _receivedRequests = MutableStateFlow<ReceivedRequestsUiState>(
        ReceivedRequestsUiState.Loading
    )
    val receivedRequests = _receivedRequests.asStateFlow()

    private val _sentRequests = MutableStateFlow<SentRequestsUiState>(SentRequestsUiState.Loading)
    val sentRequests = _sentRequests.asStateFlow()

    init {
        observeReceivedRequests()
        observeSentRequests()
    }

    fun acceptContactRequest(requestId: String) {
        viewModelScope.launch {
            try {
                contactRequestRepository.acceptContactRequest(requestId)
            } catch (e: Exception) {
                Log.e("AllRequestsViewModel", e.message ?: "Failed to accept request")
            }
        }
    }

    fun declineContactRequest(requestId: String) {
        viewModelScope.launch {
            try {
                contactRequestRepository.declineContactRequest(requestId)
            } catch (e: Exception) {
                Log.e("AllRequestsViewModel", e.message ?: "Failed to decline request")
            }
        }
    }

    fun withDrawContactRequest(request: SentRequestUiModel) {
        viewModelScope.launch {
            try {
                val senderUserId = auth.currentUser?.uid ?: return@launch

                contactRequestRepository.withdrawContactRequest(
                    requestId = request.requestId,
                    senderUserId = senderUserId,
                    receiverUserId = request.receiver.uid
                )
            } catch (e: Exception) {
                Log.e("AllRequestsViewModel", e.message ?: "Failed to withdraw request")
            }
        }
    }

    private fun observeReceivedRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch

            contactRequestRepository
                .observePendingRequests(currentUserId)
                .collect { requests ->
                    val uiModels = requests.map { request ->
                        PendingRequestUiModel(
                            requestId = request.id,
                            user = userRepository.getUserById(request.senderUserId)
                        )
                    }

                    _receivedRequests.value =
                        if (uiModels.isEmpty()) {
                            ReceivedRequestsUiState.Empty
                        } else {
                            ReceivedRequestsUiState.Success(uiModels)
                        }
                }
        }
    }

    private fun observeSentRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch

            contactRequestRepository
                .observeSentRequests(currentUserId)
                .collect { requests ->
                    val uiModels = requests.map { request ->
                        SentRequestUiModel(
                            requestId = request.id,
                            receiver = userRepository.getUserById(request.receiverUserId),
                            status = request.status
                        )
                    }

                    _sentRequests.value =
                        if (uiModels.isEmpty()) {
                            SentRequestsUiState.Empty
                        } else {
                            SentRequestsUiState.Success(uiModels)
                        }
                }
        }
    }

}