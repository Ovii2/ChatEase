package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatease.presentation.ui.theme.darkLavender
import com.example.chatease.presentation.ui.theme.lightLavender

@Composable
fun MediaBubbleContainer(
    modifier: Modifier = Modifier,
    isSentByCurrentUser: Boolean,
    onForwardClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = if (isSentByCurrentUser) {
        if (isSystemInDarkTheme()) darkLavender else lightLavender
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSentByCurrentUser) {
                MediaForwardArrow(
                    onForwardClick = onForwardClick,
                    isSentByCurrentUser = true,
                    backGroundColor = backgroundColor
                )
            }
            content()

            if (!isSentByCurrentUser) {
                MediaForwardArrow(
                    onForwardClick = onForwardClick,
                    isSentByCurrentUser = false,
                    backGroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
            }
        }
    }
}