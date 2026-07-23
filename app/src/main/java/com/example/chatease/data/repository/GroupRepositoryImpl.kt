package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.GroupDto
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.repository.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : GroupRepository {

    companion object {
        private const val GROUP = "group"
        const val USER_IDS = "userIds"
        const val ADMIN_IDS = "adminIds"
    }

    override suspend fun createGroup(
        conversationId: String,
        userIds: List<String>,
        adminIds: List<String>,
        name: String,
        ownerId: String,
        imageUrl: String?
    ): String {
        val groupRef = firestore
            .collection(GROUP)
            .document(conversationId)

        val group = Group(
            conversationId = conversationId,
            userIds = userIds,
            adminIds = adminIds,
            ownerId = ownerId,
            name = name,
            imageUrl = imageUrl
        )

        groupRef.set(group).await()

        return conversationId
    }

    override suspend fun getGroupByConversationId(conversationId: String): Group {
        val snapshot = firestore
            .collection(GROUP)
            .document(conversationId)
            .get()
            .await()

        return snapshot.toObject(GroupDto::class.java)?.toDomain()
            ?: throw IllegalStateException("Group not found")
    }

    override suspend fun getGroups(currentUserId: String): List<Group> {
        val snapshot = firestore
            .collection(GROUP)
            .whereArrayContains(USER_IDS, currentUserId)
            .get()
            .await()

        return snapshot.toObjects(GroupDto::class.java).map { it.toDomain() }
    }

    override suspend fun addAdmin(conversationId: String, userId: String) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User is not authenticated")

        val group = getGroupByConversationId(conversationId)

        check(group.ownerId == currentUserId) {
            "Only group owner can add admin"
        }

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(ADMIN_IDS, FieldValue.arrayUnion(userId))
            .await()
    }
}
