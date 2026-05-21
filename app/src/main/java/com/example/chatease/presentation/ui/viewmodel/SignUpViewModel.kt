package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatease.R
import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.enums.UserStatus
import com.example.chatease.presentation.ui.state.SignUpUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState = _uiState.asStateFlow()

    companion object {
        const val USERS = "users"
    }

    fun signUp(fullName: String, email: String, password: String) {
        _uiState.value = SignUpUiState.Loading

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener

                val capitalizedFullName = fullName
                    .trim()
                    .split(" ")
                    .joinToString(" ") { name ->
                        name.lowercase().replaceFirstChar { character ->
                            character.uppercase()
                        }
                    }

                val user = UserDto(
                    uid = userId,
                    fullName = capitalizedFullName,
                    email = email.trim(),
                    imageUrl = null,
                    status = UserStatus.OFFLINE.name
                )

                firestore.collection(USERS)
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener { _uiState.value = SignUpUiState.Success }
                    .addOnFailureListener { exception ->
                        _uiState.value = SignUpUiState.Error(
                            message = exception.message,
                            messageRes = R.string.fail_save_user
                        )
                    }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        _uiState.value = SignUpUiState.Error(messageRes = R.string.email_in_use)
                    }

                    else -> {
                        _uiState.value = SignUpUiState.Error(
                            message = exception.message,
                            messageRes = R.string.fail_sing_up
                        )
                    }
                }
            }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}