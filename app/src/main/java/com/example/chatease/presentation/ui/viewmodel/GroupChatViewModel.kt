package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupChatUiState>(GroupChatUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadGroupConversation(conversationId: String) {
        _uiState.value = GroupChatUiState.Loading
        observeGroupMessages(conversationId)
        observeUsers(conversationId)
    }

    private fun observeUsers(conversationId: String) {
        viewModelScope.launch {
            try {
                val conversation =
                    conversationRepository.getConversation(conversationId)

                val userFlows = conversation.participantIds.map { id ->
                    userRepository.observeUser(id)
                }

                combine(userFlows) { users ->
                    users.toList()
                }.collect { members ->
                    val currentState = _uiState.value

                    if (currentState is GroupChatUiState.Success) {
                        _uiState.value =
                            currentState.copy(members = members)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = GroupChatUiState.Error(
                    message = e.message.orEmpty()
                )
            }
        }
    }

    private fun observeGroupMessages(conversationId: String) {
        viewModelScope.launch {
            try {
                val groupFlow = groupRepository.observeGroup(conversationId)
                val messagesFlow = conversationRepository.observeMessages(conversationId)

                combine(groupFlow, messagesFlow) { group, messages ->
                    val currentUserId = auth.currentUser?.uid.orEmpty()
                    val removedAt = group.removedAtByUserId[currentUserId]

                    val visibleMessages = if (currentUserId in group.userIds) {
                        messages
                    } else {
                        messages.filter { message ->
                            removedAt != null && message.timeStamp <= removedAt
                        }
                    }
                    group to visibleMessages

                }.collect { (group, messages) ->
                    val currentState = _uiState.value
                    _uiState.value = if (currentState is GroupChatUiState.Success) {
                        currentState.copy(
                            group = group,
                            messages = messages
                        )
                    } else {
                        GroupChatUiState.Success(
                            group = group,
                            members = emptyList(),
                            messages = messages
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = GroupChatUiState.Error(
                    message = e.message.orEmpty()
                )
            }
        }
    }

}
