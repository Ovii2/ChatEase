package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ContactDto
import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    companion object {
        private const val USERS = "users"
        private const val STATUS = "status"
        private const val CONTACTS = "contacts"
        private const val BLOCKED_USER_IDS = "blockedUserIds"
        private const val USER_IDS = "userIds"
        private const val FCM_TOKEN = "fcmToken"
    }

    override suspend fun updateUserStatus(
        userId: String,
        status: UserPresenceStatus
    ) {
        firestore.collection(USERS)
            .document(userId)
            .update(STATUS, status.name)
            .await()
    }

    override suspend fun getUserById(userId: String): User {
        val document = firestore
            .collection(USERS)
            .document(userId)
            .get()
            .await()

        val userDto = document.toObject(UserDto::class.java)
        return userDto?.toDomain() ?: throw Exception("User not found")
    }

    override suspend fun searchUsers(query: String): List<User> {
        val currentUserId = auth.currentUser?.uid ?: ""
        val currentUser = getUserById(currentUserId)
        val blockedUserIds = currentUser.blockedUserIds

        val snapshot = firestore
            .collection(USERS)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(UserDto::class.java)?.toDomain()
        }.filter { user ->
            user.uid !in blockedUserIds
                    && user.uid != currentUserId
                    && user.fullName.contains(
                other = query,
                ignoreCase = true
            )
        }
    }

    override fun observeUser(userId: String): Flow<User> = callbackFlow {
        val listener = firestore
            .collection(USERS)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(UserDto::class.java)?.toDomain()
                user?.let(::trySend)
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun isUserConnected(otherUserId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        val contacts = firestore
            .collection(CONTACTS)
            .get()
            .await()
            .toObjects(ContactDto::class.java)

        return contacts.any { contact ->
            currentUserId in contact.userIds && otherUserId in contact.userIds
        }
    }

    override suspend fun isUserInContacts(otherUserId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        val snapshot = firestore
            .collection(CONTACTS)
            .whereArrayContains(USER_IDS, currentUserId)
            .get()
            .await()

        return snapshot.documents.any { document ->
            val contactDto = document.toObject(ContactDto::class.java)
            contactDto?.userIds?.contains(otherUserId) == true
        }
    }

    override suspend fun blockUser(userId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        if (currentUserId == userId) {
            return
        }

        val contactSnapshot = firestore
            .collection(CONTACTS)
            .whereArrayContains(USER_IDS, currentUserId)
            .get()
            .await()

        val contactDocument = contactSnapshot.documents.firstOrNull { document ->
            val contact = document.toObject(ContactDto::class.java)?.toDomain()
            contact?.userIds?.contains(userId) == true
        }

        contactDocument?.reference?.delete()?.await()

        firestore
            .collection(USERS)
            .document(currentUserId)
            .update(BLOCKED_USER_IDS, FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun unblockUser(userId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        if (currentUserId == userId) return

        firestore
            .collection(USERS)
            .document(currentUserId)
            .update(BLOCKED_USER_IDS, FieldValue.arrayRemove(userId))
            .await()
    }

    override suspend fun isUserBlocked(userId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        val document = firestore
            .collection(USERS)
            .document(currentUserId)
            .get()
            .await()

        val currentUser = document.toObject(UserDto::class.java)?.toDomain() ?: return false

        return userId in currentUser.blockedUserIds
    }

    override suspend fun isBlockedByUser(userId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        val snapshot = firestore
            .collection(USERS)
            .document(userId)
            .get()
            .await()

        val otherUser = snapshot.toObject(UserDto::class.java)?.toDomain() ?: return false

        return currentUserId in otherUser.blockedUserIds
    }

    override fun observeBlockedUsers(): Flow<List<User>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: return@callbackFlow

        val listener = firestore
            .collection(USERS)
            .document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val currentUser = snapshot?.toObject(UserDto::class.java)?.toDomain()
                val blockedUserIds = currentUser?.blockedUserIds.orEmpty()

                if (blockedUserIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                launch {
                    val blockedUsers =
                        blockedUserIds.map { blockedUserId -> getUserById(blockedUserId) }

                    trySend(blockedUsers)
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    override fun observeIsBlockedByUser(otherUserId: String): Flow<Boolean> = callbackFlow {
        val listener = firestore
            .collection(USERS)
            .document(otherUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val currentUserId = auth.currentUser?.uid ?: run {
                    trySend(false)
                    return@addSnapshotListener
                }
                val otherUser = snapshot?.toObject(UserDto::class.java)?.toDomain()
                    ?: return@addSnapshotListener

                val isBlocked = currentUserId in otherUser.blockedUserIds

                trySend(isBlocked)

            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun saveFcmToken(token: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore
            .collection(USERS)
            .document(userId)
            .set(
                mapOf(FCM_TOKEN to token),
                SetOptions.merge()
            ).await()
    }

}
