package com.example.chatease.presentation.ui.screens.shared.group

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun GroupAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    imageSize: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                shape = CircleShape
            )
            .size(imageSize),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl == null) {
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = null
            )
        } else {
//                            Image(
//                                modifier = Modifier
//                                    .clip(CircleShape)
//                                    .size(imageSize),
//                                painter = painterResource(image),
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop
//                            )
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(imageSize),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupAvatarPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupAvatar(
                    imageUrl = null
                )
            }
        }
    }
}
