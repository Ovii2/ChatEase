package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.example.chatease.presentation.ui.model.SentRequestUiModel
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

    private val _receivedRequests = MutableStateFlow<List<PendingRequestUiModel>>(emptyList())
    val receivedRequests = _receivedRequests.asStateFlow()

    private val _sentRequests = MutableStateFlow<List<SentRequestUiModel>>(emptyList())
    val sentRequests = _sentRequests.asStateFlow()

    init {
        loadReceivedRequests()
        loadSentRequests()
    }

    fun loadReceivedRequests() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val receivedRequests = contactRequestRepository.getPendingRequests(currentUserId)
                val pendingRequestUiModels = receivedRequests.map { request ->
                    PendingRequestUiModel(
                        requestId = request.id,
                        user = userRepository.getUserById(request.senderUserId)
                    )
                }
                _receivedRequests.value = pendingRequestUiModels
            } catch (e: Exception) {
                Log.e("AllRequestsViewModel", e.message ?: "Failed to load received requests")
            }
        }
    }

    fun loadSentRequests() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val sentRequests = contactRequestRepository.getSentRequests(currentUserId)
                val sentRequestsUiModels = sentRequests.map { request ->
                    SentRequestUiModel(
                        requestId = request.id,
                        receiver = userRepository.getUserById(request.receiverUserId),
                        status = request.status
                    )
                }
                _sentRequests.value = sentRequestsUiModels
            } catch (e: Exception) {
                Log.e("AllRequestsViewModel", e.message ?: "Failed to load sent requests")
            }
        }
    }
}