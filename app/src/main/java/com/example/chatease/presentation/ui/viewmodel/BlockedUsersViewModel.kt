package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _blockedUsers = MutableStateFlow<List<User>>(emptyList())
    val blockedUsers = _blockedUsers.asStateFlow()

    init {
        loadBlockedUsers()
    }

    fun loadBlockedUsers() {
        viewModelScope.launch {
            try {
                userRepository.observeBlockedUsers()
                    .collect { blockedUsers ->
                        _blockedUsers.value = blockedUsers
                    }
            } catch (e: Exception) {
                Log.v("BlockedUsersViewModel", e.message ?: "Failed to load blocked users")
            }
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.unblockUser(userId)
            } catch (e: Exception) {
                Log.v("BlockedUsersViewModel", e.message ?: "Failed to unblock user")
            }
        }
    }

}