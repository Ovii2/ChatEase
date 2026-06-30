package com.example.chatease.presentation.ui.screens.privacy_security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.screens.privacy_security.components.PrivacyAndSecurityScreenContent
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun PrivacyAndSecurityScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToBlockedUsers: () -> Unit
) {
    Scaffold(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
                title = R.string.privacy_security
            )
        }) { paddingValues ->
        PrivacyAndSecurityScreenContent(
            modifier = Modifier
                .padding(paddingValues),
            onNavigateToBlockedUsers = onNavigateToBlockedUsers
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PrivacyAndSecurityScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrivacyAndSecurityScreen(
                    onBackClick = {},
                    onNavigateToBlockedUsers = {}
                )
            }
        }
    }
}
