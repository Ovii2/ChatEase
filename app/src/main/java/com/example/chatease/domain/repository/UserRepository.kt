package com.example.chatease.domain.repository

import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun updateUserStatus(
        userId: String,
        status: UserPresenceStatus
    )

    suspend fun getUserById(userId: String): User

    suspend fun searchUsers(query: String): List<User>

    fun observeUser(userId: String): Flow<User>

    suspend fun isUserConnected(otherUserId: String): Boolean

    suspend fun blockUser(userId: String)

    suspend fun unblockUser(userId: String)

    suspend fun isUserBlocked(userId: String): Boolean

    suspend fun isBlockedByUser(userId: String): Boolean

    fun observeBlockedUsers(): Flow<List<User>>

    fun observeIsBlockedByUser(otherUserId: String): Flow<Boolean>

    suspend fun saveFcmToken(token: String)

}