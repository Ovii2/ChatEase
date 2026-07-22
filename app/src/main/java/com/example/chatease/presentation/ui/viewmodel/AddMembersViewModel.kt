package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.AddMembersUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMembersViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddMembersUiState>(AddMembersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private var loadMembersJob: Job? = null

    fun loadMembers(conversationId: String) {
        loadMembersJob?.cancel()
        _uiState.value = AddMembersUiState.Loading

        loadMembersJob = viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByConversationId(conversationId)
                val contacts = contactsRepository.getContacts(currentUserId)
                val filteredContacts = contacts.filterNot { contact ->
                    val otherUserId = contact.userIds.first { it != currentUserId }
                    otherUserId in group.userIds
                }

                val usersFlows = filteredContacts.map { contact ->
                    val otherUserId = contact.userIds.first { it != currentUserId }
                    userRepository.observeUser(otherUserId)
                }

                if (usersFlows.isEmpty()) {
                    _uiState.value = AddMembersUiState.Success(
                        members = emptyList()
                    )
                    return@launch
                }

                combine(usersFlows) { users ->
                    users.toList()
                }.collect { members ->
                    _uiState.value = AddMembersUiState.Success(
                        members = members
                    )
                }


            } catch (e: Exception) {
                _uiState.value = AddMembersUiState.Error(
                    message = e.message ?: "Failed to load members"
                )
            }
        }
    }
}
