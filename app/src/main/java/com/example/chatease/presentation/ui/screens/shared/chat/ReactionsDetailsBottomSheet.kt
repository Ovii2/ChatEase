package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
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
    val availableReactions = reactions.values.distinct()
    var selectedReaction by rememberSaveable { mutableStateOf<String?>(null) }
    val filteredUsers = users.filter { user ->
        val userReaction = reactions[user.uid]
        if (selectedReaction == null) userReaction != null else userReaction == selectedReaction
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {

        LazyColumn(modifier = modifier.padding(horizontal = 12.dp)) {
            item {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    CommonChip(
                        text = stringResource(R.string.all),
                        selected = selectedReaction == null,
                        onClick = {
                            selectedReaction = null
                        }
                    )
                    availableReactions.forEach { reaction ->
                        val count = reactions.values.count { it == reaction }
                        CommonChip(
                            text = "$reaction $count",
                            selected = selectedReaction == reaction,
                            onClick = { selectedReaction = reaction }
                        )
                    }
                }
            }
            items(filteredUsers) { user ->
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
            .padding(vertical = 6.dp),
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
    val users = List(30) {
        User(
            uid = it.toString(),
            fullName = "Test Test",
            email = "",
            imageUrl = null,
            status = UserPresenceStatus.ONLINE,
            blockedUserIds = emptyList()
        )
    }

    val emojis = listOf(
        "\uD83E\uDD70",
        "😊",
        "\uD83D\uDC4B",
        "\uD83D\uDE09",
        "\uD83D\uDC4D",
        "❤️"
    )
    val reactions = users.indices.associate { index ->
        index.toString() to emojis[index % emojis.size]
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
