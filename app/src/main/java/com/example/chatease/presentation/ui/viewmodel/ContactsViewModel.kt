package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.UserRepository
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
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _searchValue = MutableStateFlow("")
    val searchValue = _searchValue.asStateFlow()

    private val _searchedUsers = MutableStateFlow<List<User>>(emptyList())
    val searchedUsers = _searchedUsers.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

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
                    _searchedUsers.value = userRepository.searchUsers(query)
                    _isSearching.value = false
                }
            } catch (e: Exception) {
                _searchedUsers.value = emptyList()
                _isSearching.value = false
            }
        }
    }

}
