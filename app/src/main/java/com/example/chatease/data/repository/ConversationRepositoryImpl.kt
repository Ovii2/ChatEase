package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ConversationDto
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.repository.ConversationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConversationRepositoryImpl(val firestore: FirebaseFirestore) : ConversationRepository {

    companion object {
        private const val CONVERSATIONS = "conversations"
        private const val PARTICIPANT_IDS = "participantIds"
    }

    override suspend fun getUserConversations(userId: String): List<Conversation> {
        val snapshot = firestore
            .collection(CONVERSATIONS)
            .whereArrayContains(PARTICIPANT_IDS, userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(ConversationDto::class.java)?.toDomain()
        }
    }
}
