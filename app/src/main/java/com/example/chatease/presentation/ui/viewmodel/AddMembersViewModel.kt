package com.example.chatease.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.AddMembersUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class AddMembersViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddMembersUiState>(AddMembersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members = _members.asStateFlow()

    private val _filteredMembers = MutableStateFlow<List<User>>(emptyList())
    val filteredMembers = _filteredMembers.asStateFlow()

    private val _searchValue = MutableStateFlow("")
    val searchValue = _searchValue.asStateFlow()

    @OptIn(FlowPreview::class)
    val debouncedSearch = searchValue
        .drop(1)
        .debounce(300.milliseconds)

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private var loadMembersJob: Job? = null

    init {
        viewModelScope.launch {
            debouncedSearch.collect { query ->
                searchMembers(query)
            }
        }
    }

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
                        members = emptyList(),
                        selectedMemberIds = emptySet(),
                    )
                    return@launch
                }

                combine(usersFlows) { users ->
                    users.toList()
                }.collect { members ->
                    _members.value = members
                    searchMembers(searchValue.value)
                }


            } catch (e: Exception) {
                _uiState.value = AddMembersUiState.Error(
                    message = e.message ?: "Failed to load members"
                )
            }
        }
    }

    fun toggleMemberSelection(userId: String) {
        val state = _uiState.value as? AddMembersUiState.Success ?: return
        val updatedSelection: Set<String> = if (userId !in state.selectedMemberIds) {
            state.selectedMemberIds + userId
        } else {
            state.selectedMemberIds - userId
        }
        _uiState.value = state.copy(selectedMemberIds = updatedSelection)
    }

    fun searchMembers(query: String) {
        val selectedMemberIds =
            (_uiState.value as? AddMembersUiState.Success)?.selectedMemberIds ?: emptySet()

        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            _filteredMembers.value = _members.value
        } else {
            _filteredMembers.value = _members.value.filter { user ->
                user.fullName.contains(trimmedQuery, ignoreCase = true)
            }
        }
        _uiState.value = AddMembersUiState.Success(
            members = _filteredMembers.value,
            selectedMemberIds = selectedMemberIds
        )
    }

    fun clearSearch() {
        _searchValue.value = ""
        searchMembers("")
    }

    fun onSearchValueChange(value: String) {
        _searchValue.value = value
    }

    fun addMembers(conversationId: String) {
        val state = _uiState.value as? AddMembersUiState.Success ?: return
        val memberIds = state.selectedMemberIds.toList()

        if (memberIds.isEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                groupRepository.addMembers(conversationId, memberIds)
                loadMembers(conversationId)
            } catch (e: Exception) {
                Log.v("AddMembersViewModel", e.message ?: "Failed to add members", e)
            }
        }
    }

}
