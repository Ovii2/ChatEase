package com.example.chatease.presentation.screens.chats.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ChatsListPane(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatsListPanePreview() {
    ChatEaseTheme() {
        ChatsListPane()
    }
}