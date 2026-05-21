package com.example.chatease.presentation.screens.contacts

import android.annotation.SuppressLint
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
import com.example.chatease.presentation.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.screens.contacts.components.MyContactsSection
import com.example.chatease.presentation.screens.contacts.components.PendingRequestsSection
import com.example.chatease.presentation.screens.contacts.components.SentRequestsButton
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSentRequests: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val count = 3
    val users = listOf(
        User(
            uid = "",
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.OFFLINE
        ),
        User(
            uid = "",
            fullName = "Test Tester",
            email = "test@goodmail.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        ),
        User(
            uid = "",
            fullName = "Another User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        ),
        User(
            uid = "",
            fullName = "John Doe",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        ),
        User(
            uid = "",
            fullName = "New User",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.AWAY
        )
    )
    val pendingRequestsCount = 3

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            ContactsScreenTopBar(
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    focusManager.clearFocus()
                }
        )
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ChatSearchBar(
                    value = "",
                    onValueChange = {},
                    placeholder = R.string.search_contacts
                )
                if (count > 0) {
                    SentRequestsButton(
                        onNavigateToSentRequests = onNavigateToSentRequests,
                        count = count
                    )
                }
                if (pendingRequestsCount > 0) {
                    PendingRequestsSection(
                        onViewAllRequests = {},
                        users = users.take(3),
                        onCloseRequestClick = {},
                        onAcceptRequestClick = {},
                        pendingRequestsCount = pendingRequestsCount,
                    )
                }
                MyContactsSection(
                    users = users,
                    onContactClick = {}
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ContactsScreenPreview() {
    ChatEaseTheme() {
        Scaffold() {
            ContactsScreen(
                onNavigateToSentRequests = {},
                onBackClick = {},
            )
        }
    }
}
