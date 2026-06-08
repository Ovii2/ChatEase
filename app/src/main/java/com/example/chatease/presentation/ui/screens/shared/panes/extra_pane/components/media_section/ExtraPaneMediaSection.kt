package com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.media_section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.presentation.ui.screens.shared.panes.extra_pane.components.section_container.SectionContainer
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun ExtraPaneMediaSection(
    modifier: Modifier = Modifier,
    items: List<MediaItem>
) {
    SectionContainer(
        modifier = modifier,
        sectionTitle = R.string.media_links_more,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    MediaSectionItem(item = item)
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
    item: MediaItem
) {
//    AsyncImage(
//        modifier = Modifier
//            .clip(RoundedCornerShape(15.dp))
//            .size(85.dp),
//        contentScale = ContentScale.Crop,
//        model = item.thumbnailUrl,
//        contentDescription = null
//    )
    Image(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .size(85.dp),
        painter = painterResource(R.drawable.person),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExtraPaneMediaSectionPreview() {
    val items = List(4) {
        MediaItem(
            id = "1",
            thumbnailUrl = "https://",
            mediaUrl = "https://",
            type = MediaType.IMAGE
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ExtraPaneMediaSection(
                    items = items
                )
            }
        }
    }
}
