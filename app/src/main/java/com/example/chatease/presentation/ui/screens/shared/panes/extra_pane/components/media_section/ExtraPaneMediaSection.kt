package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.media_section

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.chatease.R
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.section_container.SectionContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.utils.toChatDateLabel
import com.example.chatease.utils.toFormattedFileSize
import com.example.chatease.utils.toTruncatedPreviewFileName

@Composable
fun ExtraPaneMediaSection(
    modifier: Modifier = Modifier,
    mediaItems: List<MediaItem>,
    currentUserId: String
) {
    SectionContainer(
        modifier = modifier,
        sectionTitle = R.string.media_links_more,
        content = {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(mediaItems.take(10)) { item ->
                    MediaSectionItem(
                        item = item,
                        currentUserId = currentUserId
                    )
                }
            }
        },
        showActionText = true,
        onActionTextClick = {}
    )
}

@Composable
fun MediaSectionItem(
    modifier: Modifier = Modifier,
    item: MediaItem,
    currentUserId: String
) {
    val context = LocalContext.current
    val formattedDate = item.timeStamp.toChatDateLabel(context)
    val extension = item.fileName
        .substringAfterLast(".", "")
        .lowercase()

    val extensionImage = when (extension) {
        "pdf" -> painterResource(R.drawable.ic_pdf)
        "docx" -> painterResource(R.drawable.ic_docx)
        "csv" -> painterResource(R.drawable.ic_csv)
        else -> if (isSystemInDarkTheme()) painterResource(R.drawable.ic_file_white) else painterResource(
            R.drawable.ic_file
        )
    }

    val senderName = if (item.senderId == currentUserId) {
        stringResource(R.string.you)
    } else {
        item.senderName
            .trim()
            .split(" ")
            .firstOrNull()
            .orEmpty()
    }


    Surface(
        modifier = modifier
            .size(
                width = 200.dp,
                height = 160.dp
            ),
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        when (item.type) {
            MediaType.FILE -> {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Image(
                            modifier = Modifier.size(40.dp),
                            painter = extensionImage,
                            contentDescription = null
                        )
                    }
                    Column {
                        Text(
                            text = item.fileName.toTruncatedPreviewFileName(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.fileSize.toFormattedFileSize(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(
                                R.string.sent_by_with_date,
                                senderName,
                                formattedDate
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }


            MediaType.IMAGE -> {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop,
                    model = item.thumbnailUrl,
                    contentDescription = null
                )
                Image(
                    modifier = modifier
                        .clip(RoundedCornerShape(15.dp)),
                    painter = painterResource(R.drawable.person),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }

            MediaType.VIDEO -> {}
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ExtraPaneMediaSectionPreview() {
    val items = List(4) {
        MediaItem(
            id = it.toString(),
            thumbnailUrl = "",
            mediaUrl = "",
            type = MediaType.FILE,
            fileName = "Test_file.pdf",
            fileSize = 12345678L,
            mimeType = "",
            senderId = "2",
            senderName = "Test Tester",
            timeStamp = System.currentTimeMillis()
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ExtraPaneMediaSection(
                    mediaItems = items,
                    currentUserId = "1",
                )
            }
        }
    }
}
