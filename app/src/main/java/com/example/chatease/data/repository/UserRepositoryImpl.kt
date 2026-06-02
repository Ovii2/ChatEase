package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val USERS = "users"
        private const val STATUS = "status"
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
        val snapshot = firestore
            .collection(USERS)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(UserDto::class.java)?.toDomain()
        }.filter { user ->
            user.fullName.contains(
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

}
