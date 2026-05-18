package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
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
        status: UserStatus
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
}