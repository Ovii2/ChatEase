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
import kotlinx.coroutines.flow.Flow
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
                val currentUserId = getCurrentUserId() ?: return@launch
                val categories = categoryRepository.getCategories()
                val conversationsFlow = createConversationsFlow(currentUserId)

                combine(
                    userRepository.observeUser(currentUserId),
                    conversationsFlow
                ) { user, conversations ->
                    HomeUiState.Success(
                        user = user,
                        categories = categories,
                        conversations = conversations,
                        unreadMessages = conversations.sumOf { conversation ->
                            conversation.unreadCount
                        }
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (exception: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = exception.message.orEmpty()
                )
            }
        }
    }

    private fun getCurrentUserId(): String? {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            _uiState.value = HomeUiState.Error("User not logged in")
        }

        return currentUserId
    }

    private fun createConversationsFlow(
        currentUserId: String
    ): Flow<List<ConversationUiModel>> {
        return conversationRepository
            .observeUserConversations(currentUserId)
            .flatMapLatest { rawConversations ->
                val allConversations = getAllConversations(
                    currentUserId = currentUserId,
                    rawConversations = rawConversations
                )

                if (allConversations.isEmpty()) {
                    return@flatMapLatest flowOf(emptyList())
                }

                createConversationUiModelsFlow(
                    conversations = allConversations,
                    currentUserId = currentUserId
                )
            }
    }

    private suspend fun getAllConversations(
        currentUserId: String,
        rawConversations: List<Conversation>
    ): List<Conversation> {
        val formerMemberGroups =
            groupRepository.getGroupsVisibleToFormerMember(currentUserId)

        val formerMemberConversations = formerMemberGroups.map { group ->
            conversationRepository.getConversation(group.conversationId)
        }

        return (rawConversations + formerMemberConversations)
            .distinctBy { conversation ->
                conversation.id
            }
    }

    private suspend fun createConversationUiModelsFlow(
        conversations: List<Conversation>,
        currentUserId: String
    ): Flow<List<ConversationUiModel>> {
        val directConversations = conversations.filter { conversation ->
            conversation.type == ConversationType.DIRECT
        }

        val groupConversations = conversations.filter { conversation ->
            conversation.type == ConversationType.GROUP
        }

        val directUserFlows = createDirectUserFlows(
            conversations = directConversations,
            currentUserId = currentUserId
        )

        val groupsByConversationId =
            getGroupsByConversationId(groupConversations)

        val groupMembersByConversationId =
            getGroupMembersByConversationId(groupsByConversationId)

        if (directUserFlows.isEmpty()) {
            return flowOf(
                mapToConversationUiModels(
                    conversations = conversations,
                    otherUserByConversationId = emptyMap(),
                    currentUserId = currentUserId,
                    groupsByConversationId = groupsByConversationId,
                    groupMembersByConversationId = groupMembersByConversationId
                )
            )
        }

        return combine(directUserFlows) { users ->
            val otherUserByConversationId =
                directConversations.zip(users).associate { (conversation, user) ->
                    conversation.id to user
                }

            mapToConversationUiModels(
                conversations = conversations,
                otherUserByConversationId = otherUserByConversationId,
                currentUserId = currentUserId,
                groupsByConversationId = groupsByConversationId,
                groupMembersByConversationId = groupMembersByConversationId
            )
        }
    }

    private fun createDirectUserFlows(
        conversations: List<Conversation>,
        currentUserId: String
    ): List<Flow<User>> {
        return conversations.mapNotNull { conversation ->
            val otherUserId = conversation.participantIds.firstOrNull { userId ->
                userId != currentUserId
            } ?: return@mapNotNull null

            userRepository.observeUser(otherUserId)
        }
    }

    private suspend fun getGroupsByConversationId(
        conversations: List<Conversation>
    ): Map<String, Group> {
        return conversations.associate { conversation ->
            conversation.id to groupRepository.getGroupByConversationId(
                conversation.id
            )
        }
    }

    private suspend fun getGroupMembersByConversationId(
        groupsByConversationId: Map<String, Group>
    ): Map<String, List<User>> {
        return groupsByConversationId.mapValues { (_, group) ->
            group.userIds.map { userId ->
                userRepository.getUserById(userId)
            }
        }
    }

    private fun mapToConversationUiModels(
        conversations: List<Conversation>,
        otherUserByConversationId: Map<String, User>,
        currentUserId: String,
        groupsByConversationId: Map<String, Group>,
        groupMembersByConversationId: Map<String, List<User>>
    ): List<ConversationUiModel> {
        return conversations.map { conversation ->
            val isGroupConversation =
                conversation.type == ConversationType.GROUP

            val otherUser = otherUserByConversationId[conversation.id]
            val group = groupsByConversationId[conversation.id]
            val isCurrentUserGroupMember =
                !isGroupConversation || currentUserId in group?.userIds.orEmpty()

            ConversationUiModel(
                conversationId = conversation.id,
                title = getConversationTitle(
                    isGroupConversation = isGroupConversation,
                    group = group,
                    otherUser = otherUser
                ),
                imageUrl = getConversationImageUrl(
                    isGroupConversation = isGroupConversation,
                    group = group,
                    otherUser = otherUser
                ),
                participants = getConversationParticipants(
                    conversationId = conversation.id,
                    isGroupConversation = isGroupConversation,
                    otherUser = otherUser,
                    groupMembersByConversationId =
                        groupMembersByConversationId
                ),
                lastMessage = if (isCurrentUserGroupMember) conversation.lastMessage else "",
                timestamp = if (isCurrentUserGroupMember) conversation.timestamp else 0L,
                unreadCount = if (isCurrentUserGroupMember) conversation.unreadCounts[currentUserId]
                    ?: 0 else 0,
                isGroup = isGroupConversation,
                isBlockedByOtherUser = otherUser?.blockedUserIds?.contains(currentUserId) ?: false,
                isCurrentUserGroupMember = isCurrentUserGroupMember,
            )
        }
    }

    private fun getConversationTitle(
        isGroupConversation: Boolean,
        group: Group?,
        otherUser: User?
    ): String {
        return if (isGroupConversation) {
            group?.name.orEmpty()
        } else {
            otherUser?.fullName.orEmpty()
        }
    }

    private fun getConversationImageUrl(
        isGroupConversation: Boolean,
        group: Group?,
        otherUser: User?
    ): String? {
        return if (isGroupConversation) {
            group?.imageUrl
        } else {
            otherUser?.imageUrl
        }
    }

    private fun getConversationParticipants(
        conversationId: String,
        isGroupConversation: Boolean,
        otherUser: User?,
        groupMembersByConversationId: Map<String, List<User>>
    ): List<User> {
        return if (isGroupConversation) {
            groupMembersByConversationId[conversationId].orEmpty()
        } else {
            listOfNotNull(otherUser)
        }
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }
}
