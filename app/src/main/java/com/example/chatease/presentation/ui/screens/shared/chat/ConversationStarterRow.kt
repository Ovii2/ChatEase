package com.example.chatease.presentation.ui.screens.shared.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.data.local.datasource.ConversationStarterDataSource
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ConversationStarterRow(
    modifier: Modifier = Modifier,
    onStarterClick: (String) -> Unit
) {
    val starters = ConversationStarterDataSource.conversationStarters

    LazyRow(modifier = modifier.padding(vertical = 4.dp)) {
        items(starters) { starter ->
            val starterText = stringResource(starter.text)
            CommonChip(
                text = stringResource(starter.text),
                selected = false,
                onClick = { onStarterClick(starterText) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConversationStarterRowPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConversationStarterRow(
                    onStarterClick = {}
                )
            }
        }
    }
}
