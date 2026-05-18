package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.domain.repository.CategoryRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.HomeUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val currentUserId = auth.currentUser?.uid

                if (currentUserId == null) {
                    _uiState.value = HomeUiState.Error(
                        message = "User not logged in"
                    )
                    return@launch
                }
                userRepository.updateUserStatus(
                    userId = currentUserId,
                    status = UserStatus.ONLINE
                )

                _uiState.value = HomeUiState.Success(
                    user = userRepository.getUserById(currentUserId),
                    categories = categoryRepository.getCategories(),
                    conversations = conversationRepository.getUserConversations(currentUserId),
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: ""
                )
            }
        }
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }

    fun refreshConversations() {

    }
}