package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chatease.data.local.datasource.ChatNavigationItemsDataSource
import com.example.chatease.presentation.ui.navigation.Screens

fun NavigationSuiteScope.chatNavigationSuiteItems(
    currentRoute: String,
    unreadMessages: Int,
    pendingRequests: Int,
    onDestinationClick: (String) -> Unit,
    itemColors: NavigationSuiteItemColors

) {
    ChatNavigationItemsDataSource.items.forEach { item ->
        val badgeCount = when (item.route) {
            Screens.Home.route -> unreadMessages
            Screens.Contacts.route -> pendingRequests
            else -> 0
        }

        val showBadge = badgeCount > 0
        val isSelected = currentRoute == item.route

        item(
            selected = isSelected,
            onClick = { onDestinationClick(item.route) },
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
                    if (item.icon != null) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    } else if (item.image != null) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(item.image),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            label = {
                Text(
                    text = stringResource(item.label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            },
            colors = itemColors
        )
    }
}
