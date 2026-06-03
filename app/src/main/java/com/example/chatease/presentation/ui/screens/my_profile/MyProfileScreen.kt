package com.example.chatease.presentation.ui.screens.my_profile

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.ProfileStat
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.contacts.components.ContactsScreenTopBar
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileStatsRow
import com.example.chatease.presentation.ui.screens.my_profile.components.MyProfileTopSection
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val actionIcon =
        if (isSystemInDarkTheme()) Icons.Outlined.LightMode else Icons.Outlined.DarkMode

    val user = User(
        uid = "1",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.AWAY
    )

    val stats = List(3) {
        ProfileStat(
            value = it.toString(),
            label = R.string.chats
        )
    }

    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            ContactsScreenTopBar(
                onBackClick = onBackClick,
                actionIcon = actionIcon,
                onActionIconClick = {}
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            MyProfileTopSection(
                user = user
            )
            MyProfileStatsRow(
                stats = stats
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyProfileScreenPreview() {
    ChatEaseTheme {
        Scaffold {
            Column {
                MyProfileScreen(
                    onBackClick = {}
                )
            }
        }
    }
}