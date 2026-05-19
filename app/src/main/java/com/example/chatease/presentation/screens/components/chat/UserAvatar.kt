package com.example.chatease.presentation.screens.components.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.theme.avatarGradients
import com.example.chatease.presentation.ui.theme.awayYellow
import com.example.chatease.presentation.ui.theme.awayYellowDark
import com.example.chatease.presentation.ui.theme.successGreenDark
import com.example.chatease.presentation.ui.theme.successGreenLight
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
    val name = user.fullName.split(" ")[0]
    val lastName = user.fullName.split(" ")[1]
    val firstNameLetter = name.first().uppercase()
    val lastNameFirstLetter = lastName.first().uppercase()

    val gradients = avatarGradients()
    val avatarGradient = gradients[user.uid.hashCode().absoluteValue % gradients.size]

    val statusColor = when (user.status) {
        UserStatus.ONLINE -> if (isSystemInDarkTheme()) successGreenDark else successGreenLight
        UserStatus.AWAY -> if (isSystemInDarkTheme()) awayYellowDark else awayYellow
        UserStatus.OFFLINE -> Color.Transparent
    }
    Box() {
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
                    text = "$firstNameLetter$lastNameFirstLetter",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = initialsFontSize),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        } else {
//            AsyncImage(
//                modifier = Modifier
//                    .size(avatarSize)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop,
//                model = user.imageUrl,
//                contentDescription = null
//            )
            Image(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.person),
                contentDescription = null
            )
        }
        if (user.status != UserStatus.OFFLINE && showStatus) {
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
                    .background(color = statusColor)
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
        status = UserStatus.ONLINE
    )
    ChatEaseTheme() {
        UserAvatar(
            user = user
        )
    }
}