package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StartChatFab(
    modifier: Modifier = Modifier,
    onStartNewChat: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onStartNewChat,
        shape = CircleShape
    ) {
        Icon(
            modifier = Modifier.size(35.dp),
            imageVector = Icons.Outlined.Add,
            contentDescription = null
        )
    }
}