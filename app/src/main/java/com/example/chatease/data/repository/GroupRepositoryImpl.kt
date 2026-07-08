package com.example.chatease.data.repository

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
        name: String,
        ownerId: String,
        memberIds: List<String>,
        imageUrl: String?
    ): String {
        val groupRef = firestore.collection(GROUP).document()
        val groupId = groupRef.id

        val group = Group(
            conversationId = groupId,
            ownerId = ownerId,
            name = name,
            imageUrl = imageUrl
        )

        groupRef.set(group).await()

        return groupId
    }


}