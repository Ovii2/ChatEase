package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.User
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NewChatGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val conversationRepository: ConversationRepository,
    private val imageUtils: ImageUtils
) : ViewModel() {

    private val _groupName = MutableStateFlow("")
    val groupName = _groupName.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members = _members.asStateFlow()

    private val _removedMemberIds = MutableStateFlow<Set<String>>(emptySet())
    val removedMemberIds = _removedMemberIds.asStateFlow()

    private val _suggestedGroupName = MutableStateFlow("")
    val suggestedGroupName = _suggestedGroupName.asStateFlow()

    private val _groupImageUri = MutableStateFlow<Uri?>(null)
    val groupImageUri = _groupImageUri.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage = _isUploadingImage.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow("friends")
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    fun createGroup(onGroupCreated: (String) -> Unit) {
        viewModelScope.launch {
            val participantIds = (members.value.map { it.uid } + currentUserId).distinct()
            try {
                val conversationId = createGroupConversation(participantIds)
                val imageUrl = groupImageUri.value?.let { imageUri ->
                    groupRepository.uploadGroupProfileImage(
                        conversationId = conversationId,
                        imageUri = imageUri
                    )
                }
                groupRepository.createGroup(
                    conversationId = conversationId,
                    userIds = participantIds,
                    adminIds = listOf(currentUserId),
                    name = groupName.value,
                    ownerId = currentUserId,
                    imageUrl = imageUrl,
                    categoryId = selectedCategoryId.value,
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

    fun updateGroupProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            delay(1000.milliseconds)
            try {
                _groupImageUri.value = validateGroupImage(imageUri)
            } catch (e: Exception) {
                Log.e("NewChatGroupViewModel", e.message ?: "Failed to prepare group image", e)
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    private suspend fun validateGroupImage(imageUri: Uri): Uri {
        val compressedUri = withContext(Dispatchers.IO) {
            imageUtils.compressImage(imageUri)
        }

        val isValidSize = withContext(Dispatchers.IO) {
            imageUtils.isFileSizeValid(compressedUri)
        }

        require(isValidSize) {
            "Image is too large"
        }

        return compressedUri
    }

    private suspend fun createGroupConversation(participantIds: List<String>): String {
        return conversationRepository.createGroupConversation(participantIds)
    }

}
