package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationOptionsBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onLeaveGroup: () -> Unit,
    onDeleteConversation: () -> Unit,
    isGroup: Boolean
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ConversationOptionsItem(
                onClick = onDeleteConversation,
                icon = Icons.Filled.Delete,
                text = R.string.delete_conversation,
                isDestructive = true,
            )
            if (isGroup) {
                ConversationOptionsItem(
                    onClick = onLeaveGroup,
                    icon = Icons.AutoMirrored.Default.ExitToApp,
                    text = R.string.leave_group,
                    isDestructive = true,
                )
            }
        }
    }
}

@Composable
fun ConversationOptionsItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    @StringRes text: Int,
    isDestructive: Boolean
) {
    val color =
        if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color

        )
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConversationOptionsBottomSheetPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConversationOptionsBottomSheet(
                    onDismiss = {},
                    onLeaveGroup = {},
                    onDeleteConversation = {},
                    isGroup = false,
                )
            }
        }
    }
}