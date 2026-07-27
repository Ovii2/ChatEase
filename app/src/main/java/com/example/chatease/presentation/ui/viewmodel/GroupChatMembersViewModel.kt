package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatMembersUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatMembersViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GroupChatMembersUiState>(GroupChatMembersUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private val _usersInContacts = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val usersInContacts = _usersInContacts.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private var loadMembersJob: Job? = null

    fun loadMembers(conversationId: String) {
        loadMembersJob?.cancel()
        _uiState.value = GroupChatMembersUiState.Loading

        loadMembersJob = viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByConversationId(conversationId)
                val memberFlows = group.userIds.map { userId ->
                    userRepository.observeUser(userId)
                }

                if (memberFlows.isEmpty()) {
                    _uiState.value = GroupChatMembersUiState.Success(
                        members = emptyList(),
                        adminIds = emptyList(),
                        ownerId = ""
                    )
                    return@launch
                }

                combine(memberFlows) { users ->
                    users.toList()
                }.collect { members ->
                    _uiState.value = GroupChatMembersUiState.Success(
                        members = members,
                        adminIds = group.adminIds,
                        ownerId = group.ownerId
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GroupChatMembersUiState.Error(
                    message = e.message ?: "Failed to load group members"
                )
            }
        }
    }

    fun checkIfMemberIsInContacts(memberId: String) {
        if (memberId in _usersInContacts.value) return

        viewModelScope.launch {
            try {
                val isUserInContacts = userRepository.isUserInContacts(memberId)
                _usersInContacts.value += (memberId to isUserInContacts)
            } catch (e: Exception) {
                Log.v(
                    "GroupChatMembersViewModel",
                    e.message ?: "Failed to check if user is in contacts",
                    e
                )
            }
        }
    }

    fun addAdmin(conversationId: String, userId: String) {
        viewModelScope.launch {
            try {
                groupRepository.promoteToAdmin(conversationId, userId)
                loadMembers(conversationId)
            } catch (e: Exception) {
                Log.v("AddMembersViewModel", e.message ?: "Failed to add admin")
            }
        }
    }

    fun removeAdmin(conversationId: String, userId: String) {
        viewModelScope.launch {
            try {
                groupRepository.demoteFromAdmin(conversationId, userId)
                loadMembers(conversationId)
            } catch (e: Exception) {
                Log.v("AddMembersViewModel", e.message ?: "Failed to remove admin")
            }
        }
    }

    fun removeMember(conversationId: String, userId: String) {
        viewModelScope.launch {
            try {
                groupRepository.removeMember(conversationId, userId)
                loadMembers(conversationId)
            } catch (e: Exception) {
                Log.v("AddMembersViewModel", e.message ?: "Failed to remove member")
            }
        }
    }
}
