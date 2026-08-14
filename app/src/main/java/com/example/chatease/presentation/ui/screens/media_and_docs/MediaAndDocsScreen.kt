package com.example.chatease.presentation.ui.screens.media_and_docs

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.chatease.R
import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.presentation.ui.screens.shared.chat.CommonTopBar
import com.example.chatease.presentation.ui.screens.shared.error.CommonErrorDisplay
import com.example.chatease.presentation.ui.screens.shared.loading.CustomCircularProgressIndicator
import com.example.chatease.presentation.ui.state.MediaAndDocsUiState
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.example.chatease.presentation.ui.viewmodel.MediaAndDocsViewModel
import com.example.chatease.utils.toFormattedFileSize

@Composable
fun MediaAndDocsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    mediaAndDocsViewModel: MediaAndDocsViewModel = hiltViewModel(),
    conversationId: String
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val uiState by mediaAndDocsViewModel.uiState.collectAsState()
    var selectedDocId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationId) {
        mediaAndDocsViewModel.loadMediaItems(conversationId)
    }

    Scaffold(
        modifier = modifier
            .systemBarsPadding(),
        topBar = {
            CommonTopBar(
                onBackClick = onBackClick,
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

        when (val state = uiState) {
            MediaAndDocsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CustomCircularProgressIndicator()
                }
            }

            is MediaAndDocsUiState.Success -> {
                when (selectedTabIndex) {
                    0 -> {
                        val mediaItems = state.mediaItems.filter {
                            it.type == MediaType.IMAGE || it.type == MediaType.VIDEO
                        }

                        if (mediaItems.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.no_media_title),
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Text(
                                    text = stringResource(R.string.no_media_body),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(8.dp),
                                columns = GridCells.Adaptive(minSize = 130.dp)
                            ) {
                                items(mediaItems) { mediaItem ->
                                    AsyncImage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(3f / 2f)
                                            .padding(2.dp),
                                        model = mediaItem.mediaUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
//                                    Image(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .aspectRatio(3f / 2f)
//                                            .padding(2.dp),
//                                        painter = painterResource(R.drawable.person),
//                                        contentDescription = null,
//                                        contentScale = ContentScale.Crop
//                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        val docItems = state.mediaItems.filter {
                            it.type == MediaType.FILE
                        }

                        if (docItems.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.no_docs_title),
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Text(
                                    text = stringResource(R.string.no_docs_body),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(docItems) { mediaItem ->
                                    MediaDocItem(
                                        mediaItem = mediaItem,
                                        onClick = { id ->
                                            selectedDocId = id
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
                val selectedDoc = state.mediaItems.firstOrNull { it.id == selectedDocId }

                selectedDoc?.let { mediaItem ->
                    DocDetailsDialog(
                        onDismiss = { selectedDocId = null },
                        mediaItem = mediaItem,
                        onDownloadClick = {}
                    )
                }
            }

            is MediaAndDocsUiState.Error -> {
                CommonErrorDisplay(
                    showActionButton = true,
                    onRetryClick = { mediaAndDocsViewModel.loadMediaItems(conversationId) }
                )
            }
        }
    }
}


@Composable
fun MediaDocItem(
    modifier: Modifier = Modifier,
    mediaItem: MediaItem,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick(mediaItem.id) }
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.outline_docs_24),
                contentDescription = null
            )
            Column {
                Text(
                    text = mediaItem.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = mediaItem.fileSize.toFormattedFileSize(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
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
                    onBackClick = {},
                    conversationId = "",
                )
            }
        }
    }
}