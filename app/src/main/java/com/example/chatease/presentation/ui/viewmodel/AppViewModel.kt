package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.repository.CallRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val callRepository: CallRepository
) : ViewModel() {

    private val _incomingCall = MutableStateFlow<Call?>(null)
    val incomingCall = _incomingCall.asStateFlow()


    fun observeIncomingCall() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            callRepository.observeIncomingCall(userId)
                .collect { call ->
                    _incomingCall.value = call
                }
        }
    }
}
