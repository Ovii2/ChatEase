package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ConversationDto
import com.example.chatease.data.remote.dto.MessageDto
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.repository.ConversationRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ConversationRepositoryImpl(val firestore: FirebaseFirestore) : ConversationRepository {

    companion object {
        private const val CONVERSATIONS = "conversations"
        private const val PARTICIPANT_IDS = "participantIds"
        private const val MESSAGES = "messages"
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

    override suspend fun getConversation(conversationId: String): Conversation {
        val snapshot = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .get()
            .await()

        return snapshot.toObject(ConversationDto::class.java)?.toDomain()
            ?: throw IllegalStateException("Conversation not found")
    }

    override suspend fun getMessages(conversationId: String): List<Message> {
        val snapshot = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .collection(MESSAGES)
            .get()
            .await()

        return mapDocuments(snapshot) { document ->
            document.toObject(MessageDto::class.java)?.toDomain()
        }
    }

    private fun <T> mapDocuments(
        snapshot: QuerySnapshot,
        mapper: (DocumentSnapshot) -> T?
    ): List<T> {
        return snapshot.documents.mapNotNull(mapper)
    }
}
