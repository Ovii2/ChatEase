package com.example.chatease.presentation.ui.screens.media_and_docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.R
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MediaAndDocsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val mediaItems = List(20) {
        MediaItem(
            id = it.toString(),
            thumbnailUrl = "",
            mediaUrl = "",
            type = listOf(MediaType.FILE, MediaType.IMAGE).random(),
            fileName = "file_2123123.pdf",
            fileSize = 123456L,
            mimeType = "",
            senderId = "1",
            senderName = "Test Test",
            timeStamp = System.currentTimeMillis()
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                onBackClick = {},
                title = R.string.media_docs
            )
        },
        bottomBar = {
            MediaAndDocsTabRow(
                selectedTabIndex = selectedTabIndex,
                onMediaClick = { selectedTabIndex = 0 },
                onDocsClick = { selectedTabIndex = 1 }
            )
        }
    ) { paddingValues ->
        Text("text")
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MediaAndDocsScreenPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MediaAndDocsScreen(
                    onBackClick = {}
                )
            }
        }
    }
}