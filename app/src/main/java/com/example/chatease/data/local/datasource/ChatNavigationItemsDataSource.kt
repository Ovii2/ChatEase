package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.People
import com.example.chatease.R
import com.example.chatease.domain.model.ChatNavigationItem
import com.example.chatease.presentation.ui.navigation.Screens

object ChatNavigationItemsDataSource {

    val items = listOf(
        ChatNavigationItem(
            route = Screens.Home.route,
            label = R.string.home,
            icon = Icons.Outlined.Forum
        ),
        ChatNavigationItem(
            route = Screens.Contacts.route,
            label = R.string.contacts,
            icon = Icons.Outlined.People
        ),
        ChatNavigationItem(
            route = Screens.Calls.route,
            label = R.string.calls,
            image = R.drawable.ic_phone
        ),
        ChatNavigationItem(
            route = Screens.MyProfile.route,
            label = R.string.my_profile,
            icon = Icons.Outlined.AccountCircle
        )
    )
}
