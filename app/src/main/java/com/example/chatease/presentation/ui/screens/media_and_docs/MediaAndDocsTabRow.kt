package com.example.chatease.presentation.ui.screens.media_and_docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MediaAndDocsTabRow(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    onMediaClick: () -> Unit,
    onDocsClick: () -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = onMediaClick
        ) {
            Text(text = stringResource(R.string.media))
        }
        Tab(
            selected = selectedTabIndex == 1,
            onClick = onDocsClick
        ) {
            Text(text = stringResource(R.string.docs))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MediaAndDocsTabRowPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MediaAndDocsTabRow(
                    selectedTabIndex = 1,
                    onMediaClick = {},
                    onDocsClick = {}
                )
            }
        }
    }
}
