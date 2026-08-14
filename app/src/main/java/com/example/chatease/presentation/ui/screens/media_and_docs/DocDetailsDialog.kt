package com.example.chatease.presentation.ui.screens.media_and_docs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.chatease.R
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toFormattedFileSize

@Composable
fun DocDetailsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    mediaItem: MediaItem,
    onDownloadClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = modifier.size(
                height = 250.dp,
                width = 300.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = mediaItem.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = mediaItem.fileSize.toFormattedFileSize())
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss_btn),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = onDownloadClick) {
                        Text(text = stringResource(R.string.download))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DocDetailsDialogPreview() {
    val mediaItem = MediaItem(
        id = "1",
        thumbnailUrl = "",
        mediaUrl = "",
        type = MediaType.FILE,
        fileName = "file_123123123_454324324_34343434f.pdf",
        fileSize = 12456L,
        mimeType = "",
        senderId = "1",
        senderName = "Test Test",
        timeStamp = System.currentTimeMillis()
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DocDetailsDialog(
                    onDismiss = {},
                    mediaItem = mediaItem,
                    onDownloadClick = {},
                )
            }
        }
    }
}