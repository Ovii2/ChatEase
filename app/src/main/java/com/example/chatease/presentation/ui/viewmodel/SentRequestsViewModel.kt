package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.SentRequestUiModel
import com.example.chatease.presentation.ui.state.SentRequestsUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentRequestsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val contactRequestRepository: ContactRequestRepository
) : ViewModel() {

    private val _sentRequests = MutableStateFlow<SentRequestsUiState>(SentRequestsUiState.Loading)
    val sentRequests = _sentRequests.asStateFlow()

    init {
        loadSentRequests()
    }

    fun loadSentRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                val requests = contactRequestRepository.getSentRequests(currentUserId)
                _sentRequests.value =
                    SentRequestsUiState.Success(requests = requests.map { request ->
                        SentRequestUiModel(
                            requestId = request.id,
                            receiver = userRepository.getUserById(request.receiverUserId),
                            status = request.status
                        )
                    }
                    )
            } catch (e: Exception) {
                _sentRequests.value = SentRequestsUiState.Error(
                    message = e.message ?: "Failed to get sent requests"
                )
                Log.e(
                    "SentRequestsViewModel",
                    e.message ?: "Failed to get sent request"
                )
            }
        }
    }

    fun withdrawContactRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val currentState = _sentRequests.value
                if (currentState is SentRequestsUiState.Success) {
                    contactRequestRepository.withdrawContactRequest(requestId)
                    val updatedRequests = currentState.requests.filterNot {
                        it.requestId == requestId
                    }
                    _sentRequests.value = if (updatedRequests.isEmpty()) {
                        SentRequestsUiState.Empty
                    } else {
                        SentRequestsUiState.Success(updatedRequests)
                    }
                }
            } catch (e: Exception) {
                _sentRequests.value = SentRequestsUiState.Error(
                    message = e.message ?: "Failed to withdraw request"
                )
            }
        }
    }

}
