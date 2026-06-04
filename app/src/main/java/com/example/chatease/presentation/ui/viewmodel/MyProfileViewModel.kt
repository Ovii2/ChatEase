package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.R
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.ProfileStatUiModel
import com.example.chatease.presentation.ui.state.MyProfileUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyProfileUiState>(MyProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val stats = listOf(
        ProfileStatUiModel(
            value = "1",
            label = R.string.chats
        ),
        ProfileStatUiModel(
            value = "1",
            label = R.string.groups
        ),
        ProfileStatUiModel(
            value = "1",
            label = R.string.contacts
        )
    )

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch

                userRepository.observeUser(currentUserId)
                    .collect { user ->
                        _uiState.value = MyProfileUiState.Success(
                            user = user,
                            stats = stats,
                            isUploadingImage = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = MyProfileUiState.Error(
                    message = e.message ?: "Failed to load current user"
                )
            }
        }
    }

}
