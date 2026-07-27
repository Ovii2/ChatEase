package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.GroupDto
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.repository.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : GroupRepository {

    companion object {
        private const val GROUP = "group"
        const val USER_IDS = "userIds"
        const val ADMIN_IDS = "adminIds"
        const val CONVERSATIONS = "conversations"
        const val PARTICIPANT_IDS = "participantIds"
        const val VISIBLE_TO_USER_IDS = "visibleToUserIds"
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
            visibleToUserIds = emptyList(),
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

    override suspend fun getGroupsVisibleToFormerMember(currentUserId: String): List<Group> {
        val snapshot = firestore
            .collection(GROUP)
            .whereArrayContains(VISIBLE_TO_USER_IDS, currentUserId)
            .get()
            .await()

        return snapshot.toObjects(GroupDto::class.java).map { it.toDomain() }
    }

    override suspend fun promoteToAdmin(conversationId: String, userId: String) {
        checkIfUserIsGroupOwner(conversationId, "Only group owner can add admin")

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(ADMIN_IDS, FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun demoteFromAdmin(conversationId: String, userId: String) {
        checkIfUserIsGroupOwner(conversationId, "Only group owner can remove admin")

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(ADMIN_IDS, FieldValue.arrayRemove(userId))
            .await()

    }

    override suspend fun removeMember(conversationId: String, userId: String) {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User is not authenticated")

        val group = getGroupByConversationId(conversationId)
        val currentUserIsOwner = currentUserId == group.ownerId
        val currentUserIsAdmin = currentUserId in group.adminIds
        val targetUserIsOwner = userId == group.ownerId
        val targetUserIsAdmin = userId in group.adminIds

        check(!targetUserIsOwner) {
            "Owner cannot be removed"
        }

        check(currentUserIsOwner || currentUserIsAdmin) {
            "Only owner or admins can remove members"
        }

        check(currentUserIsOwner || !targetUserIsAdmin) {
            "Admins cannot remove other admins"
        }

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(
                mapOf(
                    USER_IDS to FieldValue.arrayRemove(userId),
                    ADMIN_IDS to FieldValue.arrayRemove(userId),
                    VISIBLE_TO_USER_IDS to FieldValue.arrayUnion(userId)
                )
            )
            .await()
    }

    override suspend fun addMembers(
        conversationId: String,
        memberIds: List<String>
    ) {
        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(USER_IDS, FieldValue.arrayUnion(*memberIds.toTypedArray()))
            .await()
    }

    override fun observeGroup(conversationId: String): Flow<Group> = callbackFlow {
        val listener = firestore
            .collection(GROUP)
            .document(conversationId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                val group = snapshot?.toObject(GroupDto::class.java)?.toDomain()
                    ?: return@addSnapshotListener

                trySend(group)
            }
        awaitClose {
            listener.remove()
        }
    }

    private suspend fun checkIfUserIsGroupOwner(conversationId: String, message: String): Group {
        val currentUserId =
            auth.currentUser?.uid ?: throw IllegalStateException("User is not authenticated")

        val group = getGroupByConversationId(conversationId)

        check(group.ownerId == currentUserId) {
            message
        }

        return group
    }
}
