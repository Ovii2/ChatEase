package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.UserPresenceStatus
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
    Column() {
        ExtraPaneTopSection(
            user = user
        )
    }
}

@Preview(showBackground = true, showSystemUi = true,
         device = "spec:width=411dp,height=891dp",
         uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ExtraPaneScreenPreview() {
    ChatEaseTheme {
        Scaffold {paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ExtraPaneScreen()
            }
        }
    }
}
