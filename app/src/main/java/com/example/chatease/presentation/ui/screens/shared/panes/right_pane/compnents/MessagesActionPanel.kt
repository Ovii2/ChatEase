package com.example.chatease.presentation.ui.screens.shared.panes.right_pane.compnents

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.enums.MessageType
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesActionPanel(
    modifier: Modifier = Modifier,
    isSenderCurrentUser: Boolean,
    onReplyClick: () -> Unit,
    onCopyClick: () -> Unit,
    onDownloadClick: () -> Unit,
    messageType: MessageType
) {
    Surface(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(
                        x = 0f,
                        y = 0f
                    ),
                    end = Offset(
                        x = size.width,
                        y = 0f
                    ),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Row(
            modifier = modifier
                .heightIn(max = 100.dp)
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MessageActionsItem(
                icon = Icons.AutoMirrored.Filled.Reply,
                label = R.string.reply,
                onClick = onReplyClick
            )
            if (messageType != MessageType.TEXT) {
                MessageActionsItem(
                    icon = Icons.Default.Download,
                    label = R.string.download,
                    onClick = onDownloadClick,
                    topLabelPadding = 2.dp
                )
            } else {
                MessageActionsItem(
                    icon = Icons.Default.ContentCopy,
                    label = R.string.copy,
                    onClick = onCopyClick
                )
            }
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
    onClick: () -> Unit,
    topLabelPadding: Dp = 0.dp
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
        Text(
            modifier = Modifier.padding(top = topLabelPadding),
            text = stringResource(label)
        )
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
private fun MessagesActionPanelPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MessagesActionPanel(
                    isSenderCurrentUser = true,
                    onReplyClick = {},
                    onCopyClick = {},
                    onDownloadClick = {},
                    messageType = MessageType.FILE,
                )
            }
        }
    }
}
