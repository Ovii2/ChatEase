package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.dto.ConversationDto
import com.example.chatease.data.remote.dto.MessageDto
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.Message
import com.example.chatease.domain.repository.ConversationRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ConversationRepositoryImpl(val firestore: FirebaseFirestore) : ConversationRepository {

    companion object {
        private const val CONVERSATIONS = "conversations"
        private const val PARTICIPANT_IDS = "participantIds"
        private const val MESSAGES = "messages"
        private const val LAST_MESSAGE = "lastMessage"
        private const val TIMESTAMP = "timestamp"
        private const val MESSAGE_TIMESTAMP = "timeStamp"
        private const val SEEN_BY = "seenBy"
        private const val REACTIONS = "reactions"
        private const val UNREAD_COUNTS = "unreadCounts"
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

    override suspend fun createConversation(participantIds: List<String>): String {
        val conversationId = firestore
            .collection(CONVERSATIONS)
            .document()
            .id

        val conversationDto = ConversationDto(
            id = conversationId,
            participantIds = participantIds,
            lastMessage = "",
            timestamp = System.currentTimeMillis(),
            unreadCounts = participantIds.associateWith { 0 }
        )

        firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .set(conversationDto)
            .await()

        return conversationId
    }

    override suspend fun getExistingConversationId(participantIds: List<String>): String? {
        val sortedParticipantIds = participantIds.sorted()

        val snapshot = firestore
            .collection(CONVERSATIONS)
            .whereArrayContains(PARTICIPANT_IDS, sortedParticipantIds.first())
            .get()
            .await()

        val existingConversation = snapshot.documents.firstOrNull { document ->
            val conversation = document.toObject(ConversationDto::class.java)
            conversation?.participantIds?.sorted() == sortedParticipantIds
        }

        return existingConversation?.id
    }

    override fun observeUserConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val listener = firestore
            .collection(CONVERSATIONS)
            .whereArrayContains(PARTICIPANT_IDS, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val conversations = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(ConversationDto::class.java)?.toDomain()
                } ?: emptyList()

                trySend(conversations)
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun sendMessage(message: Message) {
        val messageId = firestore
            .collection(CONVERSATIONS)
            .document(message.conversationId)
            .collection(MESSAGES)
            .document()
            .id

        val conversationSnapshot = firestore
            .collection(CONVERSATIONS)
            .document(message.conversationId)
            .get()
            .await()

        val conversation = conversationSnapshot.toObject(ConversationDto::class.java) ?: return
        val receiverIds = conversation.participantIds.filter { participantId ->
            participantId != message.senderId
        }

        val updates = mutableMapOf<String, Any>(
            LAST_MESSAGE to message.text,
            TIMESTAMP to message.timeStamp
        )

        receiverIds.forEach { receiverId ->
            updates["$UNREAD_COUNTS.$receiverId"] = FieldValue.increment(1)
        }

        firestore
            .collection(CONVERSATIONS)
            .document(message.conversationId)
            .collection(MESSAGES)
            .document(messageId)
            .set(message.copy(messageId = messageId))
            .await()

        conversationSnapshot.reference
            .update(updates)
            .await()
    }

    override fun observeMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .collection(MESSAGES)
            .orderBy(MESSAGE_TIMESTAMP)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.let {
                    mapDocuments(it) { document ->
                        document.toObject(MessageDto::class.java)?.toDomain()
                    }
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun markMessagesAsSeen(
        conversationId: String,
        currentUserId: String
    ) {
        val messagesSnapshot = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .collection(MESSAGES)
            .get()
            .await()

        val unseenIncomingMessages = messagesSnapshot.filter { document ->
            val message = document.toObject(MessageDto::class.java)

            message != null &&
                    message.senderId != currentUserId &&
                    currentUserId !in message.seenBy
        }

        unseenIncomingMessages.forEach { document ->
            document.reference
                .update(SEEN_BY, FieldValue.arrayUnion(currentUserId))
                .await()
        }

        if (unseenIncomingMessages.isNotEmpty()) {
            firestore
                .collection(CONVERSATIONS)
                .document(conversationId)
                .update("$UNREAD_COUNTS.$currentUserId", 0)
                .await()
        }
    }

    override suspend fun addReactionToMessage(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ) {
        val documentSnapshot = firestore
            .collection(CONVERSATIONS)
            .document(conversationId)
            .collection(MESSAGES)
            .document(messageId)
            .get()
            .await()

        val message = documentSnapshot.toObject(MessageDto::class.java) ?: return

        val updatedReactions = message.reactions.toMutableMap().apply {
            put(userId, reaction)
        }

        documentSnapshot.reference
            .update(REACTIONS, updatedReactions)
            .await()
    }


    private fun <T> mapDocuments(
        snapshot: QuerySnapshot,
        mapper: (DocumentSnapshot) -> T?
    ): List<T> {
        return snapshot.documents.mapNotNull(mapper)
    }
}
