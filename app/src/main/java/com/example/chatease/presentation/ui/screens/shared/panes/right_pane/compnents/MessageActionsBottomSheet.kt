package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Translate
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
fun MessageActionsBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    isSenderCurrentUser: Boolean
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismiss
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 24.dp
                ),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MessageActionsItem(
                icon = Icons.AutoMirrored.Filled.Reply,
                label = R.string.reply,
                onClick = {}
            )
            MessageActionsItem(
                icon = Icons.Default.ContentCopy,
                label = R.string.copy,
                onClick = {}
            )
            if (isSenderCurrentUser) {
                MessageActionsItem(
                    icon = Icons.Default.Delete,
                    label = R.string.unsend,
                    onClick = {}
                )
            } else {
                MessageActionsItem(
                    icon = Icons.Default.Translate,
                    label = R.string.translate,
                    onClick = {}
                )
            }
            MessageActionsItem(
                icon = Icons.Default.Dehaze,
                label = R.string.more,
                onClick = {}
            )
        }
    }
}

@Composable
fun MessageActionsItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes label: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = stringResource(label))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessageActionsItemPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MessageActionsItem(
                    icon = Icons.AutoMirrored.Filled.Reply,
                    label = R.string.reply,
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MessageActionsBottomSheetPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MessageActionsBottomSheet(
                    onDismiss = {},
                    isSenderCurrentUser = true,
                )
            }
        }
    }
}