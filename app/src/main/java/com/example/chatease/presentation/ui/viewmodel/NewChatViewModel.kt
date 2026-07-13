package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val contactsRepository: ContactsRepository,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _filteredUsers = MutableStateFlow<List<User>>(emptyList())
    val filteredUsers = _filteredUsers.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val contacts = contactsRepository.getContacts(currentUserId)
                val users = contacts.map { contact ->
                    val otherUserId = contact.userIds.first {
                        it != currentUserId
                    }
                    userRepository.getUserById(otherUserId)
                }
                _users.value = users
                _filteredUsers.value = users
                _contacts.value = contacts
            } catch (e: Exception) {
                Log.e("NewChatViewModel", e.message ?: "Failed to load contacts")
            }
        }
    }

    fun createNewConversation(selectedUserId: String, onConversationCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val participantIds = listOf(currentUserId, selectedUserId).sorted()

                val conversationId =
                    conversationRepository.getExistingConversationId(participantIds)
                        ?: conversationRepository.createConversation(
                            participantIds,
                            ConversationType.DIRECT
                        )

                onConversationCreated(conversationId)
            } catch (e: Exception) {
                Log.e("NewChatViewModel", e.message ?: "Failed to start conversation")
            }
        }
    }

    fun filterUsers(query: String) {
        _filteredUsers.value =
            _users.value.filter { user -> user.fullName.contains(query, ignoreCase = true) }
    }

}
