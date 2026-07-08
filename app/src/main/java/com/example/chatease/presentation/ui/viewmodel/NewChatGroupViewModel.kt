package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
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
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _groupName = MutableStateFlow("")
    val groupName = _groupName.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members = _members.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun createGroup() {
        viewModelScope.launch {
            try {
                groupRepository.createGroup(
                    name = groupName.value,
                    ownerId = currentUserId,
                    memberIds = (members.value.map { it.uid } + currentUserId).distinct(),
                    imageUrl = null
                )
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
                    _members.value = users
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
        _members.value = _members.value.filterNot { it.uid == userId }
    }

}
