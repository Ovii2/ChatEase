package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.R
import com.example.chatease.domain.model.Contact
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.ProfileStatUiModel
import com.example.chatease.presentation.ui.state.MyProfileUiState
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val contactsRepository: ContactsRepository,
    private val conversationRepository: ConversationRepository,
    private val groupRepository: GroupRepository,
    private val imageUtils: ImageUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyProfileUiState>(MyProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadCurrentUser()
        loadContacts()
        loadConversations()
        loadGroups()
    }

    private fun buildStats() = listOf(
        ProfileStatUiModel(
            value = _conversations.value.size.toString(),
            label = R.string.chats
        ),
        ProfileStatUiModel(
            value = _groups.value.size.toString(),
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
                        val currentState = _uiState.value
                        val isUploadingImage =
                            (currentState as? MyProfileUiState.Success)?.isUploadingImage ?: false
                        _uiState.value = MyProfileUiState.Success(
                            user = user,
                            stats = buildStats(),
                            isUploadingImage = isUploadingImage
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
                val conversations = conversationRepository.getUserConversations(currentUserId)
                _conversations.value = conversations.filterNot { conversation ->
                    currentUserId in conversation.deletedFor
                }
                refreshStats()
            } catch (e: Exception) {
                Log.v(
                    "MyProfileViewModel",
                    e.message ?: "Failed to load conversations for $currentUserId"
                )
            }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            val currentUserId = currentUserId ?: return@launch
            try {
                _groups.value = groupRepository.getGroups(currentUserId)
                refreshStats()
            } catch (e: Exception) {
                Log.v(
                    "MyProfileViewModel",
                    e.message ?: "Failed to load group for $currentUserId"
                )
            }
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState !is MyProfileUiState.Success) {
                return@launch
            }

            _uiState.value = currentState.copy(
                isUploadingImage = true
            )

            try {
                val compressedUri = withContext(Dispatchers.IO) {
                    imageUtils.compressImage(imageUri)
                }
                val isValidSize = withContext(Dispatchers.IO) {
                    imageUtils.isFileSizeValid(compressedUri)
                }

                require(isValidSize) { "Image is too large" }

                userRepository.uploadProfileImage(
                    userId = userId,
                    imageUri = compressedUri
                )
            } catch (e: Exception) {
                Log.e("MyProfileViewModel", "Failed to upload profile image", e)
            } finally {
                val latestState = _uiState.value

                if (latestState is MyProfileUiState.Success) {
                    _uiState.value = latestState.copy(
                        isUploadingImage = false
                    )
                }
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
