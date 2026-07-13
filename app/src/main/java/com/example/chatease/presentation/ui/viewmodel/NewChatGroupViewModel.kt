package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewChatGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _groupName = MutableStateFlow("")
    val groupName = _groupName.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members = _members.asStateFlow()

    private val _removedMemberIds = MutableStateFlow<Set<String>>(emptySet())
    val removedMemberIds = _removedMemberIds.asStateFlow()

    private val _suggestedGroupName = MutableStateFlow("")
    val suggestedGroupName = _suggestedGroupName.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun createGroup(onGroupCreated: (String) -> Unit) {
        viewModelScope.launch {
            val participantIds = (members.value.map { it.uid } + currentUserId).distinct()
            try {
                val conversationId = createGroupConversation(participantIds)
                groupRepository.createGroup(
                    conversationId = conversationId,
                    name = groupName.value,
                    ownerId = currentUserId,
                    memberIds = participantIds,
                    imageUrl = null
                )
                onGroupCreated(conversationId)
            } catch (e: Exception) {
                Log.v("NewChatGroupViewModel", e.message ?: "Failed to create group", e)
            }
        }
    }

    fun observeMembers(userIds: List<String>) {
        viewModelScope.launch {
            try {
                combine(
                    userIds.map { userId ->
                        userRepository.observeUser(userId)
                    }
                ) { users ->
                    users.toList()
                }.collect { users ->
                    _members.value = users.filterNot { user ->
                        user.uid in _removedMemberIds.value
                    }
                }
            } catch (e: Exception) {
                Log.v("NewChatGroupViewModel", e.message ?: "Failed to observe members", e)
            }
        }
    }

    fun onGroupNameChange(value: String) {
        _groupName.value = value
    }

    fun setMembers(members: List<User>) {
        _members.value = members
    }

    fun removeMember(userId: String) {
        _removedMemberIds.value += userId
        _members.value = _members.value.filterNot { it.uid == userId }
    }

    fun suggestGroupName() {
        val prefixes = listOf(
            "The", "Alpha", "Apex", "Global", "Elite",
            "Cyber", "Quantum", "Nexus", "Pixel", "Vibe",
            "Chill", "Secret", "Daily", "Digital", "United",
            "BBQ", "Arctic", "Banana", "Cake", "Curry"
        )
        val suffixes = listOf(
            "Squad", "Hub", "Network", "Lounge", "Zone",
            "Circle", "Clan", "HQ", "Crew", "Lab",
            "Collective", "Alliance", "Chamber", "Guild", "Syndicate",
            "Team", "Gang", "Dorm", "Pool", "Alley"
        )
        val randomWord = "${prefixes.random()} ${suffixes.random()}"
        _suggestedGroupName.value = randomWord
    }

    fun refreshSuggestGroupName() {
        suggestGroupName()
    }

    fun acceptSuggestedGroupName(name: String) {
        onGroupNameChange(name)
    }

    private suspend fun createGroupConversation(participantIds: List<String>): String {
        return conversationRepository.createGroupConversation(participantIds)
    }

}
