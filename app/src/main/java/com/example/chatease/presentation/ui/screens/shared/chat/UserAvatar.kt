package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.model.enums.statusColor
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.avatarGradients
import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    user: User,
    avatarSize: Dp = 60.dp,
    statusBubbleSize: Dp = 18.dp,
    initialsFontSize: TextUnit = 24.sp,
    statusBubbleOffsetX: Dp = 1.dp,
    statusBubbleOffsetY: Dp = (-1).dp,
    showStatus: Boolean = true
) {
    val initials = user.fullName
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

    val gradients = avatarGradients()
    val avatarGradient = gradients[user.uid.hashCode().absoluteValue % gradients.size]

    Box {
        if (user.imageUrl == null) {
            Box(
                modifier = modifier
                    .size(avatarSize)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = avatarGradient
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = initialsFontSize),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = user.imageUrl,
                contentDescription = null
            )
//            Image(
//                modifier = Modifier
//                    .size(avatarSize)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop,
//                painter = painterResource(R.drawable.person),
//                contentDescription = null
//            )
        }
        if (user.status != UserPresenceStatus.OFFLINE && showStatus) {
            Box(
                modifier = Modifier
                    .size(statusBubbleSize)
                    .offset(x = statusBubbleOffsetX, y = statusBubbleOffsetY)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(color = user.status.statusColor())
                    .align(Alignment.BottomEnd)
            ) {}
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UserAvatarPreview() {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.OFFLINE
    )
    ChatEaseTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatar(
                user = user
            )
        }
    }
}