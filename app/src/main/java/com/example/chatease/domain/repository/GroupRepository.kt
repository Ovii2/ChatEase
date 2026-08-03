package com.example.chatease.domain.repository

import android.net.Uri
import com.example.chatease.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    suspend fun createGroup(
        conversationId: String,
        userIds: List<String>,
        adminIds: List<String>,
        name: String,
        ownerId: String,
        imageUrl: String?,
        categoryId: String
    ): String

    suspend fun getGroupByConversationId(conversationId: String): Group

    suspend fun getGroups(currentUserId: String): List<Group>

    suspend fun getGroupsVisibleToFormerMember(currentUserId: String): List<Group>

    suspend fun promoteToAdmin(conversationId: String, userId: String)
    suspend fun demoteFromAdmin(conversationId: String, userId: String)

    suspend fun removeMember(conversationId: String, userId: String)

    suspend fun addMembers(conversationId: String, memberIds: List<String>)

    fun observeGroup(conversationId: String): Flow<Group>

    suspend fun leaveGroup(conversationId: String, currentUserId: String)
    suspend fun leaveGroupAsOwner(conversationId: String, currentUserId: String): Boolean

    suspend fun removeFormerMemberVisibility(conversationId: String, currentUserId: String)

    suspend fun uploadGroupProfileImage(conversationId: String, imageUri: Uri): String

    suspend fun updateGroupProfileImage(conversationId: String, imageUrl: String)

    suspend fun updateGroupName(conversationId: String, groupName: String)

}