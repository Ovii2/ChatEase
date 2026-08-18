package com.example.chatease.data.repository

import android.net.Uri
import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.GroupDto
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.repository.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : GroupRepository {

    companion object {
        private const val GROUP = "group"
        private const val USER_IDS = "userIds"
        private const val ADMIN_IDS = "adminIds"
        private const val VISIBLE_TO_USER_IDS = "visibleToUserIds"
        private const val REMOVED_AT_USER_ID = "removedAtByUserId"
        private const val OWNER_ID = "ownerId"
        private const val CONVERSATIONS = "conversations"
        private const val PARTICIPANT_IDS = "participantIds"
        private const val GROUP_PROFILE_IMAGES = "group_profile_images"
        private const val IMAGE_URL = "imageUrl"
        private const val NAME = "name"
        private const val CHAT_FILES = "chat_files"
    }

    override suspend fun createGroup(
        conversationId: String,
        userIds: List<String>,
        adminIds: List<String>,
        name: String,
        ownerId: String,
        imageUrl: String?,
        categoryId: String
    ): String {
        require(userIds.size <= 50) {
            "A group can have at most 50 members"
        }
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
            imageUrl = imageUrl,
            categoryId = categoryId
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
                    VISIBLE_TO_USER_IDS to FieldValue.arrayUnion(userId),
                    "$REMOVED_AT_USER_ID.$userId" to System.currentTimeMillis()
                )
            )
            .await()
    }

    override suspend fun addMembers(
        conversationId: String,
        memberIds: List<String>
    ) {
        if (memberIds.isEmpty()) {
            return
        }

        val groupRef = firestore
            .collection(GROUP)
            .document(conversationId)

        val conversationRef = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)

        val group = getGroupByConversationId(conversationId)
        val finalMemberCount = (group.userIds + memberIds).distinct().size
        require(finalMemberCount <= 50) {
            "A group can have at most 50 members"
        }

        val updates = mutableMapOf<String, Any>(
            USER_IDS to FieldValue.arrayUnion(*memberIds.toTypedArray()),
            VISIBLE_TO_USER_IDS to FieldValue.arrayRemove(*memberIds.toTypedArray())
        )

        val batch = firestore.batch()

        memberIds.forEach { memberId ->
            updates["$REMOVED_AT_USER_ID.$memberId"] = FieldValue.delete()
        }

        batch.update(groupRef, updates)
        batch.update(
            conversationRef,
            PARTICIPANT_IDS,
            FieldValue.arrayUnion(*memberIds.toTypedArray())
        )

        batch.commit().await()
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
                if (snapshot == null || !snapshot.exists()) {
                    close(IllegalStateException("Group not found"))
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

    override suspend fun leaveGroup(conversationId: String, currentUserId: String) {
        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(
                mapOf(
                    ADMIN_IDS to FieldValue.arrayRemove(currentUserId),
                    USER_IDS to FieldValue.arrayRemove(currentUserId),
                    VISIBLE_TO_USER_IDS to FieldValue.arrayUnion(currentUserId),
                    "$REMOVED_AT_USER_ID.$currentUserId" to System.currentTimeMillis()
                )
            ).await()
    }

    override suspend fun leaveGroupAsOwner(conversationId: String, currentUserId: String): Boolean {
        val group = getGroupByConversationId(conversationId)

        check(group.ownerId == currentUserId) {
            "Only the group owner can use this function"
        }

        val remainingMemberIds = group.userIds - currentUserId

        if (remainingMemberIds.isEmpty()) {
            deleteGroupProfileImage(conversationId)
            deleteGroupChatFiles(conversationId)

            firestore
                .collection(GROUP)
                .document(conversationId)
                .delete()
                .await()

            return true
        }

        val newOwnerId =
            group.adminIds.firstOrNull { adminId ->
                adminId != currentUserId
            } ?: remainingMemberIds.random()

        val updatedAdminIds =
            (group.adminIds - currentUserId + newOwnerId).distinct()

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(
                mapOf(
                    OWNER_ID to newOwnerId,
                    ADMIN_IDS to updatedAdminIds,
                    USER_IDS to FieldValue.arrayRemove(currentUserId),
                    VISIBLE_TO_USER_IDS to FieldValue.arrayUnion(currentUserId),
                    "$REMOVED_AT_USER_ID.$currentUserId" to System.currentTimeMillis()
                )
            )
            .await()

        return false
    }

    override suspend fun removeFormerMemberVisibility(
        conversationId: String,
        currentUserId: String
    ) {
        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(
                mapOf(
                    VISIBLE_TO_USER_IDS to FieldValue.arrayRemove(currentUserId),
                    "$REMOVED_AT_USER_ID.$currentUserId" to FieldValue.delete()
                )
            )
            .await()
    }

    override suspend fun uploadGroupProfileImage(
        conversationId: String,
        imageUri: Uri
    ): String {
        val imageRef = storage.reference
            .child(GROUP_PROFILE_IMAGES)
            .child("$conversationId.jpg")

        imageRef.putFile(imageUri).await()

        return imageRef.downloadUrl
            .await()
            .toString()
    }

    override suspend fun updateGroupProfileImage(
        conversationId: String,
        imageUrl: String
    ) {
        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(IMAGE_URL, imageUrl)
            .await()
    }

    override suspend fun updateGroupName(conversationId: String, groupName: String) {
        val trimmedGroupName = groupName.trim()
        if (trimmedGroupName.length !in 5..50) {
            return
        }

        firestore
            .collection(GROUP)
            .document(conversationId)
            .update(NAME, trimmedGroupName)
            .await()
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

    private suspend fun deleteGroupProfileImage(conversationId: String) {
        try {
            storage.reference
                .child(GROUP_PROFILE_IMAGES)
                .child("$conversationId.jpg")
                .delete()
                .await()
        } catch (e: StorageException) {
            if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw e
            }
        }
    }

    private suspend fun deleteGroupChatFiles(conversationId: String) {
        try {
            val folderRef = storage.reference
                .child(CHAT_FILES)
                .child(conversationId)

            val files = folderRef.listAll().await()

            files.items.forEach { fileRef ->
                fileRef.delete().await()
            }

        } catch (e: StorageException) {
            if (e.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw e
            }
        }
    }

}
