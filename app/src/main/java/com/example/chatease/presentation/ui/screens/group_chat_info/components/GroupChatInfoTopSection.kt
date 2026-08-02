package com.example.chatease.presentation.ui.screens.group_chat_info.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.chatease.R
import com.example.chatease.domain.model.Group
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.loading.CustomCircularProgressIndicator
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupChatInfoTopSection(
    modifier: Modifier = Modifier,
    group: Group,
    members: List<User>,
    onUpdatePictureClick: () -> Unit,
    isUpdating: Boolean
) {
    val onlineMembersCount = members.count { it.status == UserPresenceStatus.ONLINE }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.clickable(
                enabled = !isUpdating,
                onClick = onUpdatePictureClick
            )
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = CircleShape
                    )
                    .size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isUpdating -> {
                        CustomCircularProgressIndicator()
                    }

                    group.imageUrl != null -> {
                        AsyncImage(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape),
                            model = group.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
//                        Image(
//                            modifier = Modifier
//                                .size(180.dp)
//                                .clip(CircleShape),
//                            painter = painterResource(R.drawable.person),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop
//                        )
                    }

                    else -> {
                        Icon(
                            modifier = Modifier.size(140.dp),
                            imageVector = Icons.Filled.Group,
                            contentDescription = null
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-5).dp, y = (-6).dp),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                shape = CircleShape,
                shadowElevation = 4.dp
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Row(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.total_members, members.size),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.total_online, onlineMembersCount),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun GroupChatInfoTopSectionPreview() {
    val group = Group(
        conversationId = "1",
        name = "Test Group",
        imageUrl = null
    )
    val members = List(12) {
        User(
            uid = it.toString(),
            fullName = "",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupChatInfoTopSection(
                    group = group,
                    members = members,
                    onUpdatePictureClick = {},
                    isUpdating = false,
                )
            }
        }
    }
}
