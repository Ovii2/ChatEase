package com.example.chatease.data.local.datasource

import com.example.chatease.R
import com.example.chatease.domain.model.ConversationStarter

object ConversationStarterDataSource {

    val conversationStarters = listOf(
        ConversationStarter(
            text = R.string.conversation_starter_1
        ),
        ConversationStarter(
            text = R.string.conversation_starter_2
        ),
        ConversationStarter(
            text = R.string.conversation_starter_3
        ),
        ConversationStarter(
            text = R.string.conversation_starter_4
        )
    )
}