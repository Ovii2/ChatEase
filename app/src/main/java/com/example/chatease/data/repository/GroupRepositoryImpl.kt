package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.GroupDto
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.repository.GroupRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore
) : GroupRepository {

    companion object {
        private const val GROUP = "group"
    }

    override suspend fun createGroup(
        conversationId: String,
        name: String,
        ownerId: String,
        memberIds: List<String>,
        imageUrl: String?
    ): String {
        val groupRef = firestore
            .collection(GROUP)
            .document(conversationId)

        val group = Group(
            conversationId = conversationId,
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


}