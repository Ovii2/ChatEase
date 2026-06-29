package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Call
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val callRepository: CallRepository,
    private val userRepository: UserRepository
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

    fun saveCurrentFcmToken() {
        viewModelScope.launch {
            try {
                FirebaseMessaging.getInstance().deleteToken().await()
                val token = FirebaseMessaging.getInstance().token.await()
                userRepository.saveFcmToken(token)
            } catch (e: Exception) {
                Log.v("AppViewModel", e.message ?: "Failed to save FCM token")
            }
        }
    }
}
