package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.R
import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
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
    private val userRepository: UserRepository,
    private val contactsRepository: ContactsRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyProfileUiState>(MyProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadCurrentUser()
        loadContacts()
        loadConversations()
    }

    private fun buildStats() = listOf(
        ProfileStatUiModel(
            value = _conversations.value.size.toString(),
            label = R.string.chats
        ),
        ProfileStatUiModel(
            value = "1",
            label = R.string.groups
        ),
        ProfileStatUiModel(
            value = _contacts.value.size.toString(),
            label = R.string.contacts
        )
    )

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val currentUserId = currentUserId ?: return@launch

                userRepository.observeUser(currentUserId)
                    .collect { user ->
                        _uiState.value = MyProfileUiState.Success(
                            user = user,
                            stats = buildStats(),
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

    fun loadContacts() {
        viewModelScope.launch {
            val currentUserId = currentUserId ?: return@launch
            try {
                _contacts.value = contactsRepository.getContacts(currentUserId)
                refreshStats()
            } catch (e: Exception) {
                Log.v(
                    "MyProfileViewModel",
                    e.message ?: "Failed to load contacts for $currentUserId"
                )
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            val currentUserId = currentUserId ?: return@launch
            try {
                _conversations.value = conversationRepository.getUserConversations(currentUserId)
                refreshStats()
            } catch (e: Exception) {
                Log.v(
                    "MyProfileViewModel",
                    e.message ?: "Failed to load conversations for $currentUserId"
                )
            }
        }
    }

    private fun refreshStats() {
        val currentState = _uiState.value
        if (currentState is MyProfileUiState.Success) {
            _uiState.value = currentState.copy(
                stats = buildStats()
            )
        }
    }

}
