package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.CooldownUiModel
import com.example.chatease.presentation.ui.model.PendingRequestUiModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val contactRequestRepository: ContactRequestRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _searchValue = MutableStateFlow("")
    val searchValue = _searchValue.asStateFlow()

    private val _searchedUsers = MutableStateFlow<List<User>>(emptyList())
    val searchedUsers = _searchedUsers.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _sentRequests = MutableStateFlow<List<String>>(emptyList())
    val sentRequests = _sentRequests.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<PendingRequestUiModel>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    private val _cooldowns = MutableStateFlow<List<CooldownUiModel>>(emptyList())
    val cooldowns = _cooldowns.asStateFlow()

    private val _receivedRequestUserIds = MutableStateFlow<List<String>>(emptyList())
    val receivedRequestUserIds = _receivedRequestUserIds.asStateFlow()

    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts = _contacts.asStateFlow()

    @OptIn(FlowPreview::class)
    val debouncedSearch = searchValue.debounce(300)

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        viewModelScope.launch {
            debouncedSearch.collectLatest { query ->
                searchUsers(query)
            }
        }
        observePendingRequests()
        getContacts()
    }

    fun onSearchValueChange(value: String) {
        _searchValue.value = value
    }

    fun clearSearch() {
        _searchValue.value = ""
        _searchedUsers.value = emptyList()
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    clearSearch()
                    _isSearching.value = false
                } else {
                    _isSearching.value = true
                    val users = userRepository.searchUsers(query)
                    loadCooldownUserIds(
                        users = users,
                        currentUserId = currentUserId ?: return@launch
                    )
                    _searchedUsers.value = users
                    _isSearching.value = false
                }
            } catch (e: Exception) {
                _searchedUsers.value = emptyList()
                _isSearching.value = false
            }
        }
    }

    fun sendContactRequest(receiverUserId: String) {
        viewModelScope.launch {
            val senderUserId = auth.currentUser?.uid ?: return@launch
            try {
                if (senderUserId == receiverUserId) {
                    return@launch
                }
                contactRequestRepository.sendContactRequest(
                    senderUserId = senderUserId,
                    receiverUserId = receiverUserId
                )
                _sentRequests.value += receiverUserId
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to send contact request"
                )
            }
        }
    }

    fun getPendingRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                val requests = contactRequestRepository.getPendingRequests(currentUserId)
                _receivedRequestUserIds.value = requests.map { request ->
                    request.senderUserId
                }
                val pendingRequestUiModels = requests.map { request ->
                    PendingRequestUiModel(
                        requestId = request.id,
                        user = userRepository.getUserById(request.senderUserId)
                    )
                }
                _pendingRequests.value = pendingRequestUiModels
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to get pending request"
                )
            }
        }
    }

    fun getSentRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                val requests = contactRequestRepository.getSentRequests(currentUserId)
                _sentRequests.value = requests.map { request ->
                    request.receiverUserId
                }
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to get sent request"
                )
            }
        }
    }

    fun acceptContactRequest(requestId: String) {
        viewModelScope.launch {
            try {
                contactRequestRepository.acceptContactRequest(requestId)
                _pendingRequests.value = _pendingRequests.value.filterNot {
                    it.requestId == requestId
                }
                getPendingRequests()
                getContacts()
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to accept contact request"
                )
            }
        }
    }

    fun declineContactRequest(requestId: String) {
        viewModelScope.launch {
            try {
                contactRequestRepository.declineContactRequest(requestId)
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to decline contact request"
                )
            }
        }
    }

    fun getContacts() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val contacts = contactsRepository.getContacts(currentUserId)
                val otherUserIds = contacts.map { contact ->
                    contact.userIds.first { userId ->
                        userId != currentUserId
                    }
                }
                val users = otherUserIds.map { userId ->
                    userRepository.getUserById(userId)
                }
                _contacts.value = users
                observeContactUsers(otherUserIds)
            } catch (e: Exception) {
                Log.e(
                    "ContactsViewModel",
                    e.message ?: "Failed to get contacts"
                )
            }
        }
    }

    private suspend fun loadCooldownUserIds(users: List<User>, currentUserId: String) {
        try {
            val activeCooldowns = users.mapNotNull { user ->
                val cooldown = contactRequestRepository.getCooldown(
                    senderUserId = currentUserId,
                    receiverUserId = user.uid
                )
                if (cooldown != null && cooldown.expiresAt > System.currentTimeMillis()) {
                    CooldownUiModel(
                        userId = user.uid,
                        expiresAt = cooldown.expiresAt
                    )
                } else {
                    null
                }
            }
            _cooldowns.value = activeCooldowns

        } catch (e: Exception) {
            Log.e("ContactsViewModel", e.message ?: "Loading cooldown user ids failed")
        }
    }

    private fun observePendingRequests() {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch

            contactRequestRepository
                .observePendingRequests(currentUserId)
                .collect { requests ->
                    val pendingUiModels = requests.map { request ->
                        PendingRequestUiModel(
                            requestId = request.id,
                            user = userRepository.getUserById(request.senderUserId)
                        )
                    }
                    _pendingRequests.value = pendingUiModels
                    _receivedRequestUserIds.value = requests.map { request ->
                        request.senderUserId
                    }
                }
        }
    }

    private fun observeContactUsers(userIds: List<String>) {
        userIds.forEach { userId ->
            viewModelScope.launch {
                userRepository.observeUser(userId)
                    .collect { updatedUser ->
                        _contacts.value = _contacts.value.map { user ->
                            if (user.uid == updatedUser.uid) {
                                updatedUser
                            } else {
                                user
                            }
                        }
                    }
            }
        }
    }
}
