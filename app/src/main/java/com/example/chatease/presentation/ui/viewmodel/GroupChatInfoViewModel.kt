package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatInfoUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatInfoViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupChatInfoUiState>(GroupChatInfoUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadGroup(conversationId: String) {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByConversationId(conversationId)
                val memberFlows = group.userIds.map { userId ->
                    userRepository.observeUser(userId)
                }

                combine(memberFlows) { users ->
                    users.toList()
                }.collect { members ->
                    _uiState.value = GroupChatInfoUiState.Success(
                        group = group,
                        members = members,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to load group"
                )
            }
        }
    }

}