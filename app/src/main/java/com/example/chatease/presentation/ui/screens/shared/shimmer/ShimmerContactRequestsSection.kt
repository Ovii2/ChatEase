package com.example.chatease.presentation.ui.screens.shared.shimmer

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerContactRequestsSection(
    modifier: Modifier = Modifier,
    columns: Int,
    isReceivedRequest: Boolean
) {
    LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
        items(10) {
            ShimmerContactRequestItem(
                modifier = modifier,
                isReceivedRequest = isReceivedRequest
            )
        }
    }
}

@Composable
fun ShimmerContactRequestItem(
    modifier: Modifier = Modifier,
    isReceivedRequest: Boolean
) {
    val backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .shimmer()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(120.dp, 24.dp)
                        .background(backgroundColor),
                )
                Box(
                    modifier = Modifier
                        .size(80.dp, 20.dp)
                        .background(backgroundColor),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isReceivedRequest) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(backgroundColor),
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(backgroundColor),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp, 50.dp)
                        .clip(RectangleShape)
                        .background(backgroundColor),
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ShimmerContactRequestItemPreview() {
    ChatEaseTheme {
        Scaffold {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShimmerContactRequestItem(
                    isReceivedRequest = false
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ShimmerContactRequestsSectionPreview() {
    ChatEaseTheme {
        Scaffold {
            Column {
                ShimmerContactRequestsSection(
                    columns = 1,
                    isReceivedRequest = true,
                )
            }
        }
    }
}