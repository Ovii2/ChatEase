package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.more_section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.data.local.datasource.ContactActionsDataSource
import com.example.chatease.domain.model.ContactActionItem
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.section_container.SectionContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneMoreSection(
    modifier: Modifier = Modifier,
    iconSize: Dp,
    onViewContactClick: () -> Unit,
    onShareContactClick: () -> Unit,
    onBlockContactClick: () -> Unit,
    onUnblockContactClick: () -> Unit,
    onDeleteConversationClick: () -> Unit,
    isConversationCreator: Boolean,
    isBlockedByMe: Boolean,
    isBlockedByOtherUser: Boolean,
) {
    val actions = ContactActionsDataSource.actions
    val visibleActions =
        actions.filter { action ->
            when (action.label) {
                R.string.view_contact -> !isBlockedByMe && !isBlockedByOtherUser
                R.string.share_contact -> !isBlockedByMe && !isBlockedByOtherUser
                R.string.block_contact -> !isBlockedByMe
                R.string.unblock_contact -> isBlockedByMe
                R.string.delete_chat -> true
                else -> true
            }
        }

    SectionContainer(
        sectionTitle = R.string.more,
        content = {
            visibleActions.forEach { action ->
                val onClick = when (action.label) {
                    R.string.view_contact -> onViewContactClick
                    R.string.share_contact -> onShareContactClick
                    R.string.block_contact -> onBlockContactClick
                    R.string.unblock_contact -> onUnblockContactClick
                    R.string.delete_chat -> onDeleteConversationClick
                    else -> {
                        {}
                    }
                }
                ExtraPaneMoreSectionItem(
                    action = action,
                    iconSize = iconSize,
                    onClick = onClick,
                )
            }
        }
    )
}

@Composable
fun ExtraPaneMoreSectionItem(
    modifier: Modifier = Modifier,
    action: ContactActionItem,
    iconSize: Dp,
    onClick: () -> Unit
) {
    val color = if (action.isDestructive) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = action.icon,
            contentDescription = stringResource(action.label),
            tint = color
        )
        Text(
            text = stringResource(action.label),
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExtraPaneMoreSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExtraPaneMoreSection(
                    iconSize = 26.dp,
                    onViewContactClick = {},
                    onShareContactClick = {},
                    onBlockContactClick = {},
                    onUnblockContactClick = {},
                    onDeleteConversationClick = {},
                    isConversationCreator = false,
                    isBlockedByMe = false,
                    isBlockedByOtherUser = false,
                )
            }
        }
    }
}
