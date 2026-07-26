package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionsDetailsBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    users: List<User>,
    reactions: Map<String, String>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        LazyColumn(modifier = modifier) {
            items(users) { user ->
                val reaction = reactions[user.uid] ?: return@items

                ReactionsDetailsItem(
                    user = user,
                    reaction = reaction,
                )
            }
        }
    }
}

@Composable
fun ReactionsDetailsItem(
    modifier: Modifier = Modifier,
    user: User,
    reaction: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.widthIn(max = 300.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserAvatar(
                user = user,
                showStatus = false
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = reaction,
            fontSize = 26.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReactionsDetailsItemPreview() {
    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE,
        blockedUserIds = emptyList()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReactionsDetailsItem(
                    user = user,
                    reaction = "\uD83E\uDD70",
                )
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
private fun ReactionsDetailsBottomSheetPreview() {
    val users = List(20) {
        User(
            uid = it.toString(),
            fullName = "Test Test",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }

    val reactions = (0..20).associate {
        it.toString() to "\uD83E\uDD70"
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReactionsDetailsBottomSheet(
                    users = users,
                    reactions = reactions,
                    onDismissRequest = {},
                )
            }
        }
    }
}
