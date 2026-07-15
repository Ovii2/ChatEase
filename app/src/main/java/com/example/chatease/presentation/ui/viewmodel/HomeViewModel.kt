package com.example.chatease.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.ConversationType
import com.example.chatease.domain.repository.CategoryRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.model.ConversationUiModel
import com.example.chatease.presentation.ui.state.HomeUiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val conversationRepository: ConversationRepository,
    private val groupRepository: GroupRepository
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
                val currentUserId = auth.currentUser?.uid ?: run {
                    _uiState.value = HomeUiState.Error("User not logged in")
                    return@launch
                }

                val categories = categoryRepository.getCategories()

                val conversationsFlow = conversationRepository
                    .observeUserConversations(currentUserId)
                    .flatMapLatest { rawConversations ->
                        if (rawConversations.isEmpty()) {
                            return@flatMapLatest flowOf(emptyList())
                        }

                        val directConversation =
                            rawConversations.filter { conversation -> conversation.type == ConversationType.DIRECT }

                        val groupConversation =
                            rawConversations.filter { conversation -> conversation.type == ConversationType.GROUP }

                        val otherUserFlows = directConversation.map { conversation ->
                            val otherUserId = conversation.participantIds.first {
                                it != currentUserId
                            }
                            userRepository.observeUser(otherUserId)
                        }

                        val groupsByConversationId =
                            groupConversation.associate { conversation ->
                                conversation.id to groupRepository.getGroupByConversationId(
                                    conversation.id
                                )
                            }

                        val groupParticipantsByConversationId =
                            groupConversation.associate { conversation ->
                                val participants =
                                    conversation.participantIds.map { userId ->
                                        userRepository.getUserById(userId)
                                    }

                                conversation.id to participants
                            }

                        if (otherUserFlows.isEmpty()) {
                            return@flatMapLatest flowOf(
                                mapToConversationUiModels(
                                    rawConversations = rawConversations,
                                    otherUserByConversationId = emptyMap(),
                                    currentUserId = currentUserId,
                                    groupsByConversationId = groupsByConversationId,
                                    groupParticipantsByConversationId = groupParticipantsByConversationId
                                )
                            )
                        } else {
                            combine(otherUserFlows) { otherUsers ->
                                val otherUserByConversationId =
                                    otherUsers.mapIndexed { index, user ->
                                        directConversation[index].id to user
                                    }.toMap()

                                mapToConversationUiModels(
                                    rawConversations = rawConversations,
                                    otherUserByConversationId = otherUserByConversationId,
                                    currentUserId = currentUserId,
                                    groupsByConversationId = groupsByConversationId,
                                    groupParticipantsByConversationId = groupParticipantsByConversationId
                                )
                            }
                        }
                    }


                combine(
                    userRepository.observeUser(currentUserId),
                    conversationsFlow
                ) { user, conversations ->

                    HomeUiState.Success(
                        user = user,
                        categories = categories,
                        conversations = conversations,
                        unreadMessages = conversations.sumOf { it.unreadCount }
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: ""
                )
            }
        }
    }

    private fun mapToConversationUiModels(
        rawConversations: List<Conversation>,
        otherUserByConversationId: Map<String, User>,
        currentUserId: String,
        groupsByConversationId: Map<String, Group>,
        groupParticipantsByConversationId: Map<String, List<User>>
    ): List<ConversationUiModel> = rawConversations.mapIndexed { index, conversation ->
        val otherUser = otherUserByConversationId[conversation.id]
        val isGroupConversation = conversation.type == ConversationType.GROUP
        val group = groupsByConversationId[conversation.id]

        val participants =
            if (isGroupConversation) groupParticipantsByConversationId[conversation.id].orEmpty() else otherUser?.let {
                listOf(it)
            } ?: emptyList()

        ConversationUiModel(
            conversationId = conversation.id,
            title = if (isGroupConversation) group?.name ?: "" else otherUser?.fullName ?: "",
            imageUrl = if (isGroupConversation) group?.imageUrl else otherUser?.imageUrl ?: "",
            participants = participants,
            lastMessage = conversation.lastMessage,
            timestamp = conversation.timestamp,
            unreadCount = conversation.unreadCounts[currentUserId] ?: 0,
            isGroup = isGroupConversation,
            isBlockedByOtherUser = otherUser?.blockedUserIds?.contains(currentUserId) ?: false
        )
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }
}
