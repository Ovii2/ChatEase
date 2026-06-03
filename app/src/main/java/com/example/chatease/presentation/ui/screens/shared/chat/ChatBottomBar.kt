package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onStartNewChat: () -> Unit,
    onNavigateToCalls: () -> Unit,
    onNavigateToProfile: () -> Unit,
    pendingRequests: Int,
    showContactsBadge: Boolean,
    unreadMessages: Int,
    showHomeBadge: Boolean
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
            selected = currentRoute == Screens.Home.route,
            onClick = onNavigateToHome,
            label = R.string.home,
            icon = Icons.Outlined.Forum,
            badgeCount = unreadMessages,
            showBadge = showHomeBadge
        )
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Contacts.route,
            onClick = onNavigateToContacts,
            label = R.string.contacts,
            icon = Icons.Outlined.People,
            badgeCount = pendingRequests,
            showBadge = showContactsBadge
        )
        FloatingActionButton(
            modifier = Modifier
                .offset(x = (5).dp, y = (-5).dp),
            onClick = onStartNewChat,
            shape = CircleShape
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Outlined.Add,
                contentDescription = null
            )
        }
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Calls.route,
            onClick = onNavigateToCalls,
            label = R.string.calls,
            image = R.drawable.ic_phone
        )
        CustomNavigationBarItem(
            selected = currentRoute == Screens.Profile.route,
            onClick = onNavigateToProfile,
            label = R.string.profile,
            icon = Icons.Outlined.AccountCircle
        )
    }
}

@Composable
fun RowScope.CustomNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    @StringRes label: Int,
    icon: ImageVector? = null,
    @DrawableRes image: Int? = null,
    badgeCount: Int = 0,
    showBadge: Boolean = badgeCount > 0
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        Badge {
                            Text(text = "$badgeCount")
                        }
                    }
                }
            ) {
                if (icon != null) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = icon,
                        contentDescription = null
                    )
                } else if (image != null) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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

            indicatorColor = Color.Transparent
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
                currentRoute = Screens.Home.route,
                onNavigateToHome = {},
                onNavigateToContacts = {},
                onStartNewChat = {},
                onNavigateToCalls = {},
                onNavigateToProfile = {},
                pendingRequests = 1,
                showContactsBadge = true,
                unreadMessages = 1,
                showHomeBadge = true,
            )
        }
    }
}