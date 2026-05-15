package com.example.chatease.presentation.screens.components.chat

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.navigation.Screens
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ChatBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigateToChats: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onStartNewChat: () -> Unit,
    onNavigateToCalls: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Chats.route,
            onClick = onNavigateToChats,
            label = R.string.chats,
            image = Icons.Outlined.Forum
        )
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Contacts.route,
            onClick = onNavigateToContacts,
            label = R.string.contacts,
            image = Icons.Outlined.People
        )
        CustomNavigationBarItem(
            selected = false,
            onClick = onStartNewChat,
            label = R.string.new_chat,
            image = Icons.Outlined.Add
        )
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Calls.route,
            onClick = onNavigateToCalls,
            label = R.string.calls,
            image = Icons.Outlined.Call
        )
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Profile.route,
            onClick = onNavigateToProfile,
            label = R.string.profile,
            image = Icons.Outlined.Person
        )
    }
}

@Composable
fun RowScope.CustomNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    @StringRes label: Int,
    image: ImageVector
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = image,
                contentDescription = null
            )
        },
        label = {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
        )
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatBottomBarPreview() {
    ChatEaseTheme() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            ChatBottomBar(
                currentRoute = Screens.Chats.route,
                onNavigateToChats = {},
                onNavigateToContacts = {},
                onStartNewChat = {},
                onNavigateToCalls = {},
                onNavigateToProfile = {}
            )
        }
    }
}