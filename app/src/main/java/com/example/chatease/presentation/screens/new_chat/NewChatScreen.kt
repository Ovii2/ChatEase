package com.example.chatease.presentation.screens.new_chat

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.screens.components.chat.ChatSearchBar
import com.example.chatease.presentation.screens.new_chat.components.AllContactsSection
import com.example.chatease.presentation.screens.new_chat.components.FrequentlyContactedSection
import com.example.chatease.presentation.screens.new_chat.components.NewChatTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val users = listOf(
        User(
            uid = "",
            fullName = "Test testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        ),
        User(
            uid = "",
            fullName = "Test testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        )
    )
    val contacts = listOf("1", "2")

//    val users = emptyList<User>()
//    val contacts = emptyList<String>()

    Scaffold(
        topBar = {
            NewChatTopBar(
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .padding(8.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    focusManager.clearFocus()
                })
        {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ChatSearchBar(
                    value = "",
                    onValueChange = {},
                    placeholder = R.string.search_contacts
                )
                if (users.isNotEmpty()) {
                    FrequentlyContactedSection(
                        users = users
                    )
                }

                if (contacts.isNotEmpty()) {
                    AllContactsSection(
                        users = users,
                        count = 3,
                        selected = true,
                        onChecked = {},
                        onStartChatClick = {},
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun NewChatScreenPreview() {
    ChatEaseTheme() {
        NewChatScreen(
            onBackClick = {}
        )
    }
}