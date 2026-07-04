package com.example.chatease.presentation.ui.screens.group_chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun SeenByRow(
    modifier: Modifier = Modifier,
    users: List<User>
) {
    val imageSize = 20.dp
    val maxVisibleUsers = 5
    val visibleUsers = users.take(maxVisibleUsers)
    val remainingCount = users.size - visibleUsers.size
    val fontSize = if (remainingCount > 9) 8.sp else 11.sp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleUsers.forEach { user ->
//            AsyncImage(
//                modifier = Modifier
//                    .size(imageSize)
//                    .clip(CircleShape),
//                model = user.imageUrl,
//                contentDescription = null,
//                contentScale = ContentScale.Crop
//            )
            Image(
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                painter = painterResource(R.drawable.person),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        if (remainingCount > 0) {
            Box(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = imageSize,
                        minHeight = imageSize
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$remainingCount",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = fontSize)
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SeenByRowPreview() {
    val users = List(6) {
        User(
            uid = it.toString(),
            fullName = "Test Test",
            email = "email@test.com",
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
                SeenByRow(
                    users = users
                )
            }
        }
    }
}
