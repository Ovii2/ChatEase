package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.data.local.datastore.auth.AuthPreferencesRepository
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.LoginUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val savedEmail = authPreferencesRepository.getEmail()
    val rememberEmail = authPreferencesRepository.getRememberEmail()

    fun login(email: String, password: String, remember: Boolean) {
        val trimmerEmail = email.trim()

        _uiState.value = LoginUiState.Loading


        auth.signInWithEmailAndPassword(trimmerEmail, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val currentUserId = auth.currentUser?.uid ?: return@launch
                    authPreferencesRepository.saveEmail(trimmerEmail, remember)

                    userRepository.updateUserStatus(
                        userId = currentUserId,
                        status = UserStatus.ONLINE
                    )
                }
                _uiState.value = LoginUiState.Success
            }
            .addOnFailureListener { exception ->
                _uiState.value = LoginUiState.Error(
                    message = exception.message ?: "Login failed"
                )
            }
    }

    fun logout() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid

            currentUserId?.let {
                userRepository.updateUserStatus(
                    userId = currentUserId,
                    status = UserStatus.OFFLINE
                )
            }
            auth.signOut()
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun onRememberMeChanged(email: String, remember: Boolean) {
        viewModelScope.launch {
            authPreferencesRepository.saveEmail(email.trim(), remember)
        }
    }
}
