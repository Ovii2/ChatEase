package com.example.chatease.presentation.screens.chats.components.panes.chat_list.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.Conversation
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.avatarGradients
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight
import com.example.chatease.utils.toChatTimeStamp
import kotlin.math.absoluteValue

@Composable
fun RecentChatsList(modifier: Modifier = Modifier) {

}

@Composable
fun RecentChatListItem(
    modifier: Modifier = Modifier,
    user: User,
    conversation: Conversation
) {
    val fullName = user.fullName.uppercase()
    val name = fullName.split(" ")[0]
    val lastName = fullName.split(" ")[1]
    val firstNameLetter = name.first()
    val lastNameFirstLetter = lastName.first()
    val gradients = avatarGradients()
    val avatarGradient = gradients[user.uid.hashCode().absoluteValue % gradients.size]

    val statusColor = when (user.status) {
        UserStatus.ONLINE -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
        UserStatus.AWAY -> if (isSystemInDarkTheme()) awayYellowDark else awayYellow
        UserStatus.OFFLINE -> Color.Transparent
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box() {
                if (user.imageUrl == null) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = avatarGradient
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$firstNameLetter $lastNameFirstLetter",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                } else {
//            AsyncImage(
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop,
//                model = user.imageUrl,
//                contentDescription = null
//            )
                    Image(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(R.drawable.person),
                        contentDescription = null
                    )
                }
                if (user.status != UserStatus.OFFLINE) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .offset(x = (1).dp, y = (-1).dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(color = statusColor)
                            .align(Alignment.BottomEnd)
                    ) {}
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.widthIn(max = 200.dp)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(start = 1.5.dp),
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = conversation.timestamp.toChatTimeStamp(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                        color = MaterialTheme.colorScheme.surface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun RecentChatsSectionPreview() {
//    ChatEaseTheme() {
//        RecentChatsList()
//    }
//}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun RecentChatListItemPreview(modifier: Modifier = Modifier) {
    val user = User(
        uid = "1",
        fullName = "Jane Cooper",
        email = "test@email.com",
        imageUrl = null,
        status = UserStatus.ONLINE
    )

    val conversation = Conversation(
        id = "1",
        participants = listOf(user),
        lastMessage = "Hey! Are we still for lunch?",
        timestamp = System.currentTimeMillis(),
        unreadCount = 9
    )
    ChatEaseTheme() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RecentChatListItem(
                user = user,
                conversation = conversation,
            )
        }
    }
}