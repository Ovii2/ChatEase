package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.chatease.presentation.ui.navigation.Screens

@Composable
fun ChatNavigationScaffold(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    currentRoute: String,
    unreadMessages: Int,
    pendingRequests: Int,
    missedCalls: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToCalls: () -> Unit,
    onNavigateToProfile: () -> Unit,
    content: @Composable () -> Unit
) {
    val customNavSuiteType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationBar
        WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }

    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = Color.Transparent

    val navigationContainerColor = MaterialTheme.colorScheme.surfaceContainerLow

    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            indicatorColor = indicatorColor
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            indicatorColor = indicatorColor
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor
        )
    )
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            chatNavigationSuiteItems(
                currentRoute = currentRoute,
                unreadMessages = unreadMessages,
                pendingRequests = pendingRequests,
                onDestinationClick = { route ->
                    when (route) {
                        Screens.Home.route -> onNavigateToHome()
                        Screens.Contacts.route -> onNavigateToContacts()
                        Screens.Calls.route -> onNavigateToCalls()
                        Screens.MyProfile.route -> onNavigateToProfile()
                    }
                },
                itemColors = itemColors,
                missedCalls = missedCalls,
            )
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = navigationContainerColor,
            navigationRailContainerColor = navigationContainerColor,
            navigationDrawerContainerColor = navigationContainerColor
        ),
        layoutType = customNavSuiteType
    ) {
        content()
    }
}
