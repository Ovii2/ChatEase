package com.example.chatease.presentation.screens.contacts

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatease.R
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.UserStatus
import com.example.chatease.presentation.screens.components.chat.ChatSearchBar
import com.example.chatease.presentation.screens.components.chat.UserAvatar
import com.example.chatease.presentation.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.screens.contacts.components.PendingItemRequestButton
import com.example.chatease.presentation.screens.contacts.components.PendingRequestsSection
import com.example.chatease.presentation.screens.contacts.components.SentRequestsButton
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    focusManager: FocusManager,
    onNavigateToSentRequests: () -> Unit
) {
    val count = 3
    val users = listOf(
        User(
            uid = "",
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        ),
        User(
            uid = "",
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        ),
        User(
            uid = "",
            fullName = "Test Testing",
            email = "test@email.com",
            imageUrl = null,
            status = UserStatus.ONLINE
        )
    )

    Scaffold(
        topBar = { ContactsScreenTopBar() }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .padding(8.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    focusManager.clearFocus()
                }
        )
        {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                ChatSearchBar(
                    value = searchValue,
                    onValueChange = onSearchValueChange,
                    placeholder = R.string.search_contacts
                )
                if (count > 0) {
                    SentRequestsButton(
                        onNavigateToSentRequests = onNavigateToSentRequests,
                        count = count
                    )
                }
                PendingRequestsSection(
                    onViewAllRequests = {},
                    users = users,
                    onCloseRequestClick = {},
                    onAcceptRequestClick = {},
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
                searchValue = "",
                onSearchValueChange = {},
                focusManager = LocalFocusManager.current,
                onNavigateToSentRequests = {},
            )
        }
    }
}
