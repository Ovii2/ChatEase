package com.example.chatease.presentation.ui.screens.group_chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.Group
import com.example.chatease.presentation.ui.screens.shared.group.GroupAvatar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    members: Int,
    group: Group
) {
    TopAppBar(
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    GroupAvatar(group = group)
                    Column(modifier = Modifier.widthIn(max = 150.dp)) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.total_members, members),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { },
                        painter = painterResource(R.drawable.ic_phone),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.ic_video_cam),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable { onBackClick() },
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = null
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupChatTopBarPreview() {
    val group = Group(
        conversationId = "1",
        name = "New Groupppppppp",
        imageUrl = null
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatTopBar(
                    onBackClick = {},
                    members = 2,
                    group = group
                )
            }
        }
    }
}
