package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.about_section.ExtraPaneAboutSection
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.top_section.ExtraPaneTopSection
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneScreen(modifier: Modifier = Modifier) {
    val user = User(
        uid = "",
        fullName = "Test Test",
        email = "test@email.com",
        imageUrl = null,
        status = UserPresenceStatus.ONLINE
    )
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 500.dp)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExtraPaneTopSection(
                    user = user
                )
                ExtraPaneAboutSection()
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ExtraPaneScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ExtraPaneScreen()
            }
        }
    }
}
