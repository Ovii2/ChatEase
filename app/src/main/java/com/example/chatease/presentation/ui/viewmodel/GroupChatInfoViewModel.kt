package com.example.chatease.presentation.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.GroupChatInfoUiState
import com.example.chatease.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GroupChatInfoViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository,
    private val imageUtils: ImageUtils,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupChatInfoUiState>(GroupChatInfoUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isOwner = MutableStateFlow(false)
    val isOwner = _isOwner.asStateFlow()

    private val _isGroupMember = MutableStateFlow(false)
    val isGroupMember = _isGroupMember.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _ownerLeaveCompleted = MutableSharedFlow<Unit>()
    val ownerLeaveCompleted = _ownerLeaveCompleted.asSharedFlow()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private var loadGroupJob: Job? = null

    fun loadGroup(conversationId: String) {
        loadGroupJob?.cancel()
        _uiState.value = GroupChatInfoUiState.Loading

        loadGroupJob = viewModelScope.launch {
            try {
                groupRepository.observeGroup(conversationId)
                    .flatMapLatest { group ->
                        val memberFlows = group.userIds.map { userId ->
                            userRepository.observeUser(userId)
                        }

                        val mediaFlow = fileRepository.observeMediaItems(conversationId)

                        if (memberFlows.isEmpty()) {
                            flowOf(
                                GroupChatInfoUiState.Success(
                                    group = group,
                                    members = emptyList(),
                                    media = emptyList()
                                )
                            )
                        } else {
                            combine(
                                combine(memberFlows) { users -> users.toList() },
                                mediaFlow
                            ) { members, media ->
                                GroupChatInfoUiState.Success(
                                    group = group,
                                    members = members,
                                    media = media
                                )
                            }
                        }
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to load group"
                )
            }
        }
    }

    fun leaveGroup(conversationId: String) {
        viewModelScope.launch {
            loadGroupJob?.cancel()
            try {
                groupRepository.leaveGroup(conversationId, currentUserId)
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to leave the group"
                )
            }
        }
    }

    fun leaveGroupAsOwner(conversationId: String) {
        viewModelScope.launch {
            loadGroupJob?.cancel()
            try {
                val shouldDeleteConversation =
                    groupRepository.leaveGroupAsOwner(conversationId, currentUserId)
                if (shouldDeleteConversation) {
                    conversationRepository.deleteConversationWithMessages(conversationId)
                }
                _ownerLeaveCompleted.emit(Unit)
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to leave the group as owner"
                )
            }
        }
    }

    fun checkIfUserIsGroupOwner(conversationId: String) {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByConversationId(conversationId)
                _isOwner.value = group.ownerId == currentUserId
            } catch (e: Exception) {
                _isOwner.value = false
            }
        }
    }

    fun checkIfUserIsGroupMember(conversationId: String) {
        viewModelScope.launch {
            try {
                val group = groupRepository.getGroupByConversationId(conversationId)
                _isGroupMember.value = currentUserId in group.userIds
            } catch (e: Exception) {
                _isGroupMember.value = false
            }
        }
    }

    fun updateGroupProfileImage(conversationId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isUpdating.value = true

            try {
                val preparedImageUri = validateGroupImage(imageUri)
                val imageUrl = groupRepository.uploadGroupProfileImage(
                    conversationId = conversationId,
                    imageUri = preparedImageUri
                )

                groupRepository.updateGroupProfileImage(
                    conversationId = conversationId,
                    imageUrl = imageUrl
                )
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to update group image"
                )
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun updateGroupName(conversationId: String, groupName: String) {
        viewModelScope.launch {
            try {
                groupRepository.updateGroupName(conversationId, groupName)
            } catch (e: Exception) {
                _uiState.value = GroupChatInfoUiState.Error(
                    error = e.message ?: "Failed to update group name"
                )
            }
        }
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

}